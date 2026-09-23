package com.example.mymoji.feature.emoji.domain.usecase

import com.example.mymoji.feature.emoji.domain.model.Emoji
import com.example.mymoji.feature.emoji.domain.repository.EmojiRepository
import javax.inject.Inject

class GetEmojisUseCase @Inject constructor(
    private val repository: EmojiRepository
) {
    suspend operator fun invoke(): List<Emoji> {
        return repository.getEmojis()
    }
}
