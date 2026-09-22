package com.example.mymoji.feature.githubuser.domain.repository

import com.example.mymoji.feature.githubuser.domain.model.GitHubUser

interface GitHubUserRepository {
    suspend fun getUser(username: String): GitHubUser
    suspend fun getAllUsers(): List<GitHubUser>
    suspend fun deleteUser(username: String)
    suspend fun getCachedUser(username: String): GitHubUser?
}
