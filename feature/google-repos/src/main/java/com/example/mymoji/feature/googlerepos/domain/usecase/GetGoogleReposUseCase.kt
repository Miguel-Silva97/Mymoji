package com.example.mymoji.feature.googlerepos.domain.usecase

import com.example.mymoji.feature.googlerepos.domain.model.GitHubRepoPage
import com.example.mymoji.feature.googlerepos.domain.repository.GitHubRepoRepository
import javax.inject.Inject

class GetGoogleReposUseCase @Inject constructor(
    private val repository: GitHubRepoRepository
) {
    suspend operator fun invoke(page: Int, pageSize: Int): GitHubRepoPage {
        return repository.getGoogleRepos(page = page, pageSize = pageSize)
    }
}
