package com.example.mymoji.feature.githubuser.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoji.feature.githubuser.domain.usecase.GetGitHubUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GitHubUserViewModel @Inject constructor(
    private val getGitHubUserUseCase: GetGitHubUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GitHubUserUiState>(GitHubUserUiState.Idle)
    val uiState: StateFlow<GitHubUserUiState> = _uiState.asStateFlow()

    fun searchUser(username: String) {
        val trimmedUsername = username.trim()
        if (trimmedUsername.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = GitHubUserUiState.Loading
            try {
                val user = getGitHubUserUseCase(trimmedUsername)
                _uiState.value = GitHubUserUiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = GitHubUserUiState.Error(message = e.localizedMessage ?: "Unknown Error occurred")
            }
        }
    }
}
