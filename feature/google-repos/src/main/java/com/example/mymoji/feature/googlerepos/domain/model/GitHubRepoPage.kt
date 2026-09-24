package com.example.mymoji.feature.googlerepos.domain.model

data class GitHubRepoPage(
    val repos: List<GitHubRepo>,
    val hasNextPage: Boolean
)
