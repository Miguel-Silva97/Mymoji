package com.example.mymoji.di

import com.example.mymoji.domain.repository.EmojiRepository
import com.example.mymoji.domain.usecase.GetEmojisUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetEmojisUseCase(repository: EmojiRepository): GetEmojisUseCase {
        return GetEmojisUseCase(repository)
    }
}
