package com.example.mymoji.feature.githubuser.domain.repository

import com.example.mymoji.feature.githubuser.domain.model.GitHubUser

interface GitHubUserRepository {
    suspend fun getUser(username: String): GitHubUser
}
