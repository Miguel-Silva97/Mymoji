package com.example.mymoji.core.data.di

import android.content.Context
import androidx.room.Room
import com.example.mymoji.core.data.EmojiDao
import com.example.mymoji.core.data.GitHubUserDao
import com.example.mymoji.core.data.MymojiDatabase
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
        return Room.databaseBuilder(context, MymojiDatabase::class.java, "mymoji.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideEmojiDao(database: MymojiDatabase): EmojiDao = database.emojiDao()

    @Provides
    @Singleton
    fun provideGitHubUserDao(database: MymojiDatabase): GitHubUserDao = database.gitHubUserDao()
}
