package com.example.mymoji.feature.emoji.data.repository

import com.example.mymoji.core.data.EmojiDao
import com.example.mymoji.core.data.EmojiEntity
import com.example.mymoji.feature.emoji.data.remote.EmojiApiService
import com.example.mymoji.feature.emoji.domain.model.Emoji
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EmojiRepositoryImplTest {

    private val apiService = mockk<EmojiApiService>()
    private val emojiDao = mockk<EmojiDao>(relaxUnitFun = true)
    private val repository = EmojiRepositoryImpl(apiService, emojiDao)

    @Test
    fun `getEmojis returns cached emojis without calling the api`() = runTest {
        coEvery { emojiDao.getAllEmojis() } returns listOf(EmojiEntity("smile", "smile.png"))

        val result = repository.getEmojis()

        assertEquals(listOf(Emoji("smile", "smile.png")), result)
        coVerify(exactly = 0) { apiService.getEmojis() }
    }

    @Test
    fun `getEmojis fetches from api, sorts by name and persists when cache is empty`() = runTest {
        coEvery { emojiDao.getAllEmojis() } returns emptyList()
        coEvery { apiService.getEmojis() } returns mapOf(
            "wink" to "wink.png",
            "heart" to "heart.png",
            "smile" to "smile.png"
        )

        val result = repository.getEmojis()

        val expected = listOf(
            Emoji("heart", "heart.png"),
            Emoji("smile", "smile.png"),
            Emoji("wink", "wink.png")
        )
        assertEquals(expected, result)
        coVerify(exactly = 1) {
            emojiDao.insertAll(expected.map { EmojiEntity(it.name, it.url) })
        }
    }

    @Test
    fun `concurrent getEmojis calls only hit the api once`() = runTest {
        var stored = emptyList<EmojiEntity>()
        coEvery { emojiDao.getAllEmojis() } answers { stored }
        coEvery { emojiDao.insertAll(any()) } answers { stored = firstArg() }
        coEvery { apiService.getEmojis() } coAnswers {
            delay(100)
            mapOf("smile" to "smile.png")
        }

        val results = List(3) { async { repository.getEmojis() } }.awaitAll()

        results.forEach { assertEquals(listOf(Emoji("smile", "smile.png")), it) }
        coVerify(exactly = 1) { apiService.getEmojis() }
    }

    @Test
    fun `getCachedEmoji returns the mapped emoji when present`() = runTest {
        coEvery { emojiDao.getEmoji("smile") } returns EmojiEntity("smile", "smile.png")

        assertEquals(Emoji("smile", "smile.png"), repository.getCachedEmoji("smile"))
    }

    @Test
    fun `getCachedEmoji returns null when missing`() = runTest {
        coEvery { emojiDao.getEmoji("missing") } returns null

        assertNull(repository.getCachedEmoji("missing"))
    }
}
