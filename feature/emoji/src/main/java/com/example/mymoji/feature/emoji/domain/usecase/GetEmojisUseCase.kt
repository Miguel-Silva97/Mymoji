package com.example.mymoji.feature.emoji.domain.usecase

import com.example.mymoji.feature.emoji.domain.model.Emoji
import com.example.mymoji.feature.emoji.domain.repository.EmojiRepository

class GetEmojisUseCase(
    private val repository: EmojiRepository
) {
    suspend operator fun invoke(): List<Emoji> {
        return repository.getEmojis()
    }
}
