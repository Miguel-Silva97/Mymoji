package com.example.mymoji.feature.emoji.di

import com.example.mymoji.feature.emoji.data.remote.EmojiApiService
import com.example.mymoji.feature.emoji.data.repository.EmojiRepositoryImpl
import com.example.mymoji.feature.emoji.domain.repository.EmojiRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EmojiModule {

    @Binds
    abstract fun bindEmojiRepository(impl: EmojiRepositoryImpl): EmojiRepository

    companion object {

        @Provides
        @Singleton
        fun provideEmojiApiService(retrofit: Retrofit): EmojiApiService {
            return retrofit.create(EmojiApiService::class.java)
        }
    }
}
