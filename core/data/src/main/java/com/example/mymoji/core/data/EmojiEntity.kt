package com.example.mymoji.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emojis")
data class EmojiEntity(
    @PrimaryKey val name: String,
    val url: String
)
