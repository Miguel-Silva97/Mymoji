package com.example.mymoji.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EmojiEntity::class], version = 1, exportSchema = false)
abstract class MymojiDatabase : RoomDatabase() {
    abstract fun emojiDao(): EmojiDao
}
