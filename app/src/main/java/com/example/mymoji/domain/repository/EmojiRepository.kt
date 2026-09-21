package com.example.mymoji.domain.repository

import com.example.mymoji.domain.model.Emoji

interface EmojiRepository {
    suspend fun getEmojis(): List<Emoji>
}
