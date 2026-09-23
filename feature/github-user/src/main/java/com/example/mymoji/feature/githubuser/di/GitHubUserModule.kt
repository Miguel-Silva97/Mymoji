package com.example.mymoji.feature.githubuser.di

import com.example.mymoji.feature.githubuser.data.remote.GitHubUserApiService
import com.example.mymoji.feature.githubuser.data.repository.GitHubUserRepositoryImpl
import com.example.mymoji.feature.githubuser.domain.repository.GitHubUserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GitHubUserModule {

    @Binds
    abstract fun bindGitHubUserRepository(impl: GitHubUserRepositoryImpl): GitHubUserRepository

    companion object {

        @Provides
        @Singleton
        fun provideGitHubUserApiService(retrofit: Retrofit): GitHubUserApiService {
            return retrofit.create(GitHubUserApiService::class.java)
        }
    }
}
