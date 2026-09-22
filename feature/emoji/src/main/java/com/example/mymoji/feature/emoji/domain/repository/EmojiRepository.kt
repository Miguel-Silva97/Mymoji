package com.example.mymoji.feature.emoji.domain.repository

import com.example.mymoji.feature.emoji.domain.model.Emoji

interface EmojiRepository {
    suspend fun getEmojis(): List<Emoji>
}
