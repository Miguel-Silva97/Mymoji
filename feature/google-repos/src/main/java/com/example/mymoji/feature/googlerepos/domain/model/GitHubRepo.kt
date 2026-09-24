package com.example.mymoji.feature.googlerepos.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class GitHubRepo(
    val name: String,
    val description: String?
)
