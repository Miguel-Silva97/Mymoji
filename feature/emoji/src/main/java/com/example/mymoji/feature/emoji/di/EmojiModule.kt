package com.example.mymoji.feature.emoji.di

import com.example.mymoji.core.data.EmojiDao
import com.example.mymoji.feature.emoji.data.remote.EmojiApiService
import com.example.mymoji.feature.emoji.data.repository.EmojiRepositoryImpl
import com.example.mymoji.feature.emoji.domain.repository.EmojiRepository
import com.example.mymoji.feature.emoji.domain.usecase.GetEmojisUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmojiModule {

    @Provides
    @Singleton
    fun provideEmojiApiService(retrofit: Retrofit): EmojiApiService {
        return retrofit.create(EmojiApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEmojiRepository(apiService: EmojiApiService, emojiDao: EmojiDao): EmojiRepository {
        return EmojiRepositoryImpl(apiService, emojiDao)
    }

    @Provides
    @Singleton
    fun provideGetEmojisUseCase(repository: EmojiRepository): GetEmojisUseCase {
        return GetEmojisUseCase(repository)
    }
}
