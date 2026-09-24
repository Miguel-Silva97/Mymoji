package com.example.mymoji.feature.githubuser.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoji.feature.githubuser.domain.model.GitHubUser
import com.example.mymoji.feature.githubuser.domain.usecase.DeleteGitHubUserUseCase
import com.example.mymoji.feature.githubuser.domain.usecase.GetGitHubUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GitHubUserListViewModel @Inject constructor(
    private val getGitHubUsersUseCase: GetGitHubUsersUseCase,
    private val deleteGitHubUserUseCase: DeleteGitHubUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GitHubUserListUiState>(GitHubUserListUiState.Idle)
    val uiState: StateFlow<GitHubUserListUiState> = _uiState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = GitHubUserListUiState.Loading
            try {
                val users = getGitHubUsersUseCase()
                _uiState.value = GitHubUserListUiState.Success(users)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = GitHubUserListUiState.Error(message = e.localizedMessage ?: "Unknown Error occurred")
            }
        }
    }

    fun deleteUser(user: GitHubUser) {
        val currentState = _uiState.value
        if (currentState !is GitHubUserListUiState.Success) return

        viewModelScope.launch {
            deleteGitHubUserUseCase(user.login)
            _uiState.update { state ->
                if (state is GitHubUserListUiState.Success) {
                    state.copy(users = state.users.filterNot { it.login == user.login })
                } else {
                    state
                }
            }
        }
    }
}
