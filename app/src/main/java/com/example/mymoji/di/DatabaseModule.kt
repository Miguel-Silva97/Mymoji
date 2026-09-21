package com.example.mymoji.di

import android.content.Context
import androidx.room.Room
import com.example.mymoji.data.local.EmojiDao
import com.example.mymoji.data.local.MymojiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMymojiDatabase(@ApplicationContext context: Context): MymojiDatabase {
        return Room.databaseBuilder(context, MymojiDatabase::class.java, "mymoji.db").build()
    }

    @Provides
    @Singleton
    fun provideEmojiDao(database: MymojiDatabase): EmojiDao {
        return database.emojiDao()
    }
}
