package com.example.mymoji.feature.githubuser.presentation

import com.example.mymoji.feature.githubuser.domain.model.GitHubUser

sealed interface GitHubUserUiState {
    object Idle : GitHubUserUiState
    object Loading : GitHubUserUiState
    data class Success(val user: GitHubUser) : GitHubUserUiState
    data class Error(val message: String) : GitHubUserUiState
}
