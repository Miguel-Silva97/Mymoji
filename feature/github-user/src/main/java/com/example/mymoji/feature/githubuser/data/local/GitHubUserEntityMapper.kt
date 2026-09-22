package com.example.mymoji.feature.githubuser.data.local

import com.example.mymoji.core.data.GitHubUserEntity
import com.example.mymoji.feature.githubuser.domain.model.GitHubUser

fun GitHubUserEntity.toDomain(): GitHubUser = GitHubUser(login = login, id = id, avatarUrl = avatarUrl)

fun GitHubUser.toEntity(): GitHubUserEntity = GitHubUserEntity(login = login, id = id, avatarUrl = avatarUrl)
