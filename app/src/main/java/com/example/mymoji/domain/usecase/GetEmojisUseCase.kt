package com.example.mymoji.domain.usecase

import com.example.mymoji.domain.model.Emoji
import com.example.mymoji.domain.repository.EmojiRepository

class GetEmojisUseCase(
    private val repository: EmojiRepository
) {
    suspend operator fun invoke(): List<Emoji> {
        return repository.getEmojis()
    }
}
