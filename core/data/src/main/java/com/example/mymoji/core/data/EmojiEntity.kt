package com.example.mymoji.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.concurrent.Immutable

@Entity(tableName = "emojis")
@Immutable
data class EmojiEntity(
    @PrimaryKey val name: String,
    val url: String
)
