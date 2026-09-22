package com.example.mymoji.feature.githubuser.domain.usecase

import com.example.mymoji.feature.githubuser.domain.repository.GitHubUserRepository

class DeleteGitHubUserUseCase(
    private val repository: GitHubUserRepository
) {
    suspend operator fun invoke(username: String) {
        repository.deleteUser(username)
    }
}
