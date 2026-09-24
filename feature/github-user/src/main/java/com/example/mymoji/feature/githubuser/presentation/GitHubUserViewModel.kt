package com.example.mymoji.feature.githubuser.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoji.core.data.LastDisplayedItem
import com.example.mymoji.core.data.LastDisplayedItemStore
import com.example.mymoji.feature.githubuser.R
import com.example.mymoji.feature.githubuser.domain.usecase.GetGitHubUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GitHubUserViewModel @Inject constructor(
    private val getGitHubUserUseCase: GetGitHubUserUseCase,
    private val lastDisplayedItemStore: LastDisplayedItemStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<GitHubUserUiState>(GitHubUserUiState.Idle)
    val uiState: StateFlow<GitHubUserUiState> = _uiState.asStateFlow()

    fun searchUser(username: String) {
        val trimmedUsername = username.trim()
        Timber.d("Trimmed username is $trimmedUsername")
        if (trimmedUsername.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = GitHubUserUiState.Loading
            try {
                val user = getGitHubUserUseCase(trimmedUsername)
                _uiState.value = GitHubUserUiState.Success(user)
                lastDisplayedItemStore.save(LastDisplayedItem.GitHubUser(user.login))
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch GitHub user '$trimmedUsername'")
                _uiState.value = GitHubUserUiState.Error(messageRes = e.toMessageRes())
            }
        }
    }

    fun onUserDeleted(login: String) {
        viewModelScope.launch { lastDisplayedItemStore.removeGitHubUser(login) }
    }

    @StringRes
    private fun Exception.toMessageRes(): Int = when ((this as? HttpException)?.code()) {
        403 -> R.string.error_rate_limited
        404 -> R.string.error_user_not_found
        else -> R.string.error_unknown
    }
}
