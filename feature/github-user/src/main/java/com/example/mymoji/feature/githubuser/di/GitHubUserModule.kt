package com.example.mymoji.feature.githubuser.di

import com.example.mymoji.core.data.GitHubUserDao
import com.example.mymoji.feature.githubuser.data.remote.GitHubUserApiService
import com.example.mymoji.feature.githubuser.data.repository.GitHubUserRepositoryImpl
import com.example.mymoji.feature.githubuser.domain.repository.GitHubUserRepository
import com.example.mymoji.feature.githubuser.domain.usecase.DeleteGitHubUserUseCase
import com.example.mymoji.feature.githubuser.domain.usecase.GetCachedGitHubUserUseCase
import com.example.mymoji.feature.githubuser.domain.usecase.GetGitHubUserUseCase
import com.example.mymoji.feature.githubuser.domain.usecase.GetGitHubUsersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GitHubUserModule {

    @Provides
    @Singleton
    fun provideGitHubUserApiService(retrofit: Retrofit): GitHubUserApiService {
        return retrofit.create(GitHubUserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubUserRepository(
        apiService: GitHubUserApiService,
        gitHubUserDao: GitHubUserDao
    ): GitHubUserRepository {
        return GitHubUserRepositoryImpl(apiService, gitHubUserDao)
    }

    @Provides
    @Singleton
    fun provideGetGitHubUserUseCase(repository: GitHubUserRepository): GetGitHubUserUseCase {
        return GetGitHubUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetGitHubUsersUseCase(repository: GitHubUserRepository): GetGitHubUsersUseCase {
        return GetGitHubUsersUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteGitHubUserUseCase(repository: GitHubUserRepository): DeleteGitHubUserUseCase {
        return DeleteGitHubUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCachedGitHubUserUseCase(repository: GitHubUserRepository): GetCachedGitHubUserUseCase {
        return GetCachedGitHubUserUseCase(repository)
    }
}
