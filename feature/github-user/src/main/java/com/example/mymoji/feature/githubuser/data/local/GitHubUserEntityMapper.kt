package com.example.mymoji.feature.githubuser.data.local

import com.example.mymoji.core.data.GitHubUserEntity
import com.example.mymoji.feature.githubuser.domain.model.GitHubUser

fun GitHubUserEntity.toDomain(): GitHubUser = GitHubUser(login = login, id = id, avatarUrl = avatarUrl)

fun List<GitHubUserEntity>.toDomain(): List<GitHubUser> = map { it.toDomain() }

fun GitHubUser.toEntity(): GitHubUserEntity = GitHubUserEntity(login = login, id = id, avatarUrl = avatarUrl)
