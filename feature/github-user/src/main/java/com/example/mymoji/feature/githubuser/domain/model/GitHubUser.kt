package com.example.mymoji.feature.githubuser.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class GitHubUser(
    val login: String,
    val id: Long,
    val avatarUrl: String
)
