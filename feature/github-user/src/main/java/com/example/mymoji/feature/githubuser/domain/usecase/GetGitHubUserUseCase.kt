package com.example.mymoji.feature.githubuser.domain.usecase

import com.example.mymoji.feature.githubuser.domain.model.GitHubUser
import com.example.mymoji.feature.githubuser.domain.repository.GitHubUserRepository
import javax.inject.Inject

class GetGitHubUserUseCase @Inject constructor(
    private val repository: GitHubUserRepository
) {
    suspend operator fun invoke(username: String): GitHubUser {
        return repository.getUser(username)
    }
}
