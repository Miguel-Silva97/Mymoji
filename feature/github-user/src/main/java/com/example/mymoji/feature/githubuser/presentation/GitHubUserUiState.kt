package com.example.mymoji.feature.githubuser.presentation

import androidx.annotation.StringRes
import com.example.mymoji.feature.githubuser.domain.model.GitHubUser

sealed interface GitHubUserUiState {
    object Idle : GitHubUserUiState
    object Loading : GitHubUserUiState
    data class Success(val user: GitHubUser) : GitHubUserUiState
    data class Error(@param:StringRes val messageRes: Int) : GitHubUserUiState
}
