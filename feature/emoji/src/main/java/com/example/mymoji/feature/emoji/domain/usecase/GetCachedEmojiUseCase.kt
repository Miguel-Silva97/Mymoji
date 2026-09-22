package com.example.mymoji.feature.emoji.domain.usecase

import com.example.mymoji.feature.emoji.domain.model.Emoji
import com.example.mymoji.feature.emoji.domain.repository.EmojiRepository

class GetCachedEmojiUseCase(
    private val repository: EmojiRepository
) {
    suspend operator fun invoke(name: String): Emoji? {
        return repository.getCachedEmoji(name)
    }
}
