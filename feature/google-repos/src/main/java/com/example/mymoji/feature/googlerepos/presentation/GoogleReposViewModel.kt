package com.example.mymoji.feature.googlerepos.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoji.feature.googlerepos.domain.usecase.GetGoogleReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GoogleReposViewModel @Inject constructor(
    private val getGoogleReposUseCase: GetGoogleReposUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GoogleReposUiState(
            page = savedStateHandle[KEY_PAGE] ?: 1,
            pageSize = savedStateHandle[KEY_PAGE_SIZE] ?: DEFAULT_PAGE_SIZE
        )
    )
    val uiState: StateFlow<GoogleReposUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadPage(_uiState.value.page, _uiState.value.pageSize)
    }

    fun nextPage() {
        val state = _uiState.value
        if (state.hasNextPage && !state.isLoading) loadPage(state.page + 1, state.pageSize)
    }

    fun previousPage() {
        val state = _uiState.value
        if (state.hasPreviousPage && !state.isLoading) loadPage(state.page - 1, state.pageSize)
    }

    fun setPageSize(size: Int) {
        if (size in PAGE_SIZE_OPTIONS && size != _uiState.value.pageSize) loadPage(1, size)
    }

    fun retry() {
        loadPage(_uiState.value.page, _uiState.value.pageSize)
    }

    private fun loadPage(page: Int, pageSize: Int) {
        savedStateHandle[KEY_PAGE] = page
        savedStateHandle[KEY_PAGE_SIZE] = pageSize
        _uiState.update {
            it.copy(page = page, pageSize = pageSize, repos = emptyList(), isLoading = true, errorMessageRes = null)
        }

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            try {
                val result = getGoogleReposUseCase(page = page, pageSize = pageSize)
                _uiState.update {
                    it.copy(repos = result.repos, hasNextPage = result.hasNextPage, isLoading = false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Failed to load Google repos page $page")
                _uiState.update { it.copy(isLoading = false, errorMessageRes = e.toMessageRes()) }
            }
        }
    }

    companion object {
        val PAGE_SIZE_OPTIONS = listOf(5, 10, 20, 50)
        const val DEFAULT_PAGE_SIZE = 10
        private const val KEY_PAGE = "page"
        private const val KEY_PAGE_SIZE = "page_size"
    }
}
