package com.example.mymoji.core.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EmojiEntity::class, GitHubUserEntity::class], version = 2, exportSchema = false)
abstract class MymojiDatabase : RoomDatabase() {
    abstract fun emojiDao(): EmojiDao
    abstract fun gitHubUserDao(): GitHubUserDao
}
