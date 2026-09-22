package com.example.mymoji.feature.emoji.data.repository

import com.example.mymoji.core.data.EmojiDao
import com.example.mymoji.feature.emoji.data.local.toDomain
import com.example.mymoji.feature.emoji.data.local.toEntity
import com.example.mymoji.feature.emoji.data.remote.EmojiApiService
import com.example.mymoji.feature.emoji.domain.model.Emoji
import com.example.mymoji.feature.emoji.domain.repository.EmojiRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

class EmojiRepositoryImpl(
    private val apiService: EmojiApiService,
    private val emojiDao: EmojiDao
) : EmojiRepository {

    // Guards the check-then-fetch sequence below so concurrent calls can't both
    // miss the cache and each burn a request against the GitHub API rate limit.
    private val mutex = Mutex()

    override suspend fun getEmojis(): List<Emoji> = mutex.withLock {
        val cachedEmojis = emojiDao.getAllEmojis()
        if (cachedEmojis.isNotEmpty()) {
            Timber.d("Returning ${cachedEmojis.size} emojis from local cache")
            return@withLock cachedEmojis.toDomain()
        }

        Timber.d("Local cache empty, fetching emojis from API")
        val rawMap = apiService.getEmojis()
        val mappedList = rawMap.map { (name, url) ->
            Emoji(name = name, url = url)
        }.sortedBy { it.name }

        emojiDao.insertAll(mappedList.toEntity())
        Timber.d("Persisted ${mappedList.size} emojis to local cache")
        return@withLock mappedList
    }
}
