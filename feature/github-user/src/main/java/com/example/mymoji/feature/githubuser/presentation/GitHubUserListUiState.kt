package com.example.mymoji.feature.githubuser.presentation

import com.example.mymoji.feature.githubuser.domain.model.GitHubUser

sealed interface GitHubUserListUiState {
    object Idle : GitHubUserListUiState
    object Loading : GitHubUserListUiState
    data class Success(val users: List<GitHubUser>) : GitHubUserListUiState
    data class Error(val message: String) : GitHubUserListUiState
}
