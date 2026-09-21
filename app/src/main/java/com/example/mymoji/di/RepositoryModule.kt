package com.example.mymoji.di

import com.example.mymoji.data.local.EmojiDao
import com.example.mymoji.data.remote.GitHubApiService
import com.example.mymoji.data.repository.EmojiRepositoryImpl
import com.example.mymoji.domain.repository.EmojiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideEmojiRepository(apiService: GitHubApiService, emojiDao: EmojiDao): EmojiRepository {
        return EmojiRepositoryImpl(apiService, emojiDao)
    }
}
