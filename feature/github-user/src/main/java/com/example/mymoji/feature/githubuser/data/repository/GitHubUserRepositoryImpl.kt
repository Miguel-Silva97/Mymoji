package com.example.mymoji.feature.githubuser.data.repository

import com.example.mymoji.core.data.GitHubUserDao
import com.example.mymoji.feature.githubuser.data.local.toDomain
import com.example.mymoji.feature.githubuser.data.local.toEntity
import com.example.mymoji.feature.githubuser.data.remote.GitHubUserApiService
import com.example.mymoji.feature.githubuser.domain.model.GitHubUser
import com.example.mymoji.feature.githubuser.domain.repository.GitHubUserRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubUserRepositoryImpl @Inject constructor(
    private val apiService: GitHubUserApiService,
    private val gitHubUserDao: GitHubUserDao
) : GitHubUserRepository {

    override suspend fun getUser(username: String): GitHubUser {
        val cachedUser = gitHubUserDao.getUser(username)
        if (cachedUser != null) {
            Timber.d("Returning GitHub user '$username' from local cache")
            return cachedUser.toDomain()
        }

        Timber.d("Cache miss for '$username', fetching from API")
        val response = apiService.getUser(username)
        val user = GitHubUser(login = response.login, id = response.id, avatarUrl = response.avatarUrl)
        gitHubUserDao.insert(user.toEntity())
        Timber.d("Persisted GitHub user '${user.login}' to local cache")
        return user
    }

    override suspend fun getAllUsers(): List<GitHubUser> {
        return gitHubUserDao.getAllUsers().toDomain()
    }

    override suspend fun deleteUser(username: String) {
        gitHubUserDao.deleteUser(username)
    }

    override suspend fun getCachedUser(username: String): GitHubUser? {
        return gitHubUserDao.getUser(username)?.toDomain()
    }
}
