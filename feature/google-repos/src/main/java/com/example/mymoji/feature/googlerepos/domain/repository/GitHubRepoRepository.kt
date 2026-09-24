package com.example.mymoji.feature.googlerepos.domain.repository

import com.example.mymoji.feature.googlerepos.domain.model.GitHubRepoPage

interface GitHubRepoRepository {
    suspend fun getGoogleRepos(page: Int, pageSize: Int): GitHubRepoPage
}
