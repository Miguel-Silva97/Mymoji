package com.example.mymoji.feature.googlerepos.presentation

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.mymoji.feature.googlerepos.domain.model.GitHubRepo

@Immutable
data class GoogleReposUiState(
    val page: Int = 1,
    val pageSize: Int = GoogleReposViewModel.DEFAULT_PAGE_SIZE,
    val repos: List<GitHubRepo> = emptyList(),
    val hasNextPage: Boolean = false,
    val isLoading: Boolean = false,
    @param:StringRes val errorMessageRes: Int? = null
) {
    val hasPreviousPage: Boolean get() = page > 1
}
