package com.example.mymoji.feature.googlerepos.di

import com.example.mymoji.feature.googlerepos.data.remote.GitHubRepoApiService
import com.example.mymoji.feature.googlerepos.data.repository.GitHubRepoRepositoryImpl
import com.example.mymoji.feature.googlerepos.domain.repository.GitHubRepoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GoogleReposModule {

    @Binds
    abstract fun bindGitHubRepoRepository(impl: GitHubRepoRepositoryImpl): GitHubRepoRepository

    companion object {

        @Provides
        @Singleton
        fun provideGitHubRepoApiService(retrofit: Retrofit): GitHubRepoApiService {
            return retrofit.create(GitHubRepoApiService::class.java)
        }
    }
}
