package com.example.mymoji.feature.googlerepos.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.mymoji.feature.googlerepos.R
import com.example.mymoji.feature.googlerepos.domain.model.GitHubRepo
import com.example.mymoji.feature.googlerepos.domain.model.GitHubRepoPage
import com.example.mymoji.feature.googlerepos.domain.repository.GitHubRepoRepository
import com.example.mymoji.feature.googlerepos.domain.usecase.GetGoogleReposUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class GoogleReposViewModelTest {

    private val repository = mockk<GitHubRepoRepository>()
    private val savedStateHandle = SavedStateHandle()

    private val kotlin = GitHubRepo(name = "kotlin", description = "Kotlin repo")
    private val android = GitHubRepo(name = "android", description = null)

    private fun createViewModel() = GoogleReposViewModel(
        getGoogleReposUseCase = GetGoogleReposUseCase(repository),
        savedStateHandle = savedStateHandle
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        coEvery { repository.getGoogleRepos(page = 1, pageSize = 10) } returns
            GitHubRepoPage(repos = listOf(kotlin), hasNextPage = true)
        coEvery { repository.getGoogleRepos(page = 2, pageSize = 10) } returns
            GitHubRepoPage(repos = listOf(android), hasNextPage = false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads the first page with 10 repos per page by default`() {
        val viewModel = createViewModel()

        assertEquals(
            GoogleReposUiState(page = 1, pageSize = 10, repos = listOf(kotlin), hasNextPage = true),
            viewModel.uiState.value
        )
    }

    @Test
    fun `nextPage loads the following page`() {
        val viewModel = createViewModel()

        viewModel.nextPage()

        assertEquals(
            GoogleReposUiState(page = 2, pageSize = 10, repos = listOf(android), hasNextPage = false),
            viewModel.uiState.value
        )
    }

    @Test
    fun `nextPage does nothing on the last page`() {
        val viewModel = createViewModel()
        viewModel.nextPage()

        viewModel.nextPage()

        assertEquals(2, viewModel.uiState.value.page)
        coVerify(exactly = 0) { repository.getGoogleRepos(page = 3, pageSize = any()) }
    }

    @Test
    fun `previousPage goes back a page`() {
        val viewModel = createViewModel()
        viewModel.nextPage()

        viewModel.previousPage()

        assertEquals(1, viewModel.uiState.value.page)
        assertEquals(listOf(kotlin), viewModel.uiState.value.repos)
    }

    @Test
    fun `previousPage does nothing on the first page`() {
        val viewModel = createViewModel()

        viewModel.previousPage()

        assertEquals(1, viewModel.uiState.value.page)
        coVerify(exactly = 1) { repository.getGoogleRepos(any(), any()) }
    }

    @Test
    fun `changing the page size goes back to page 1 with the new size`() {
        coEvery { repository.getGoogleRepos(page = 1, pageSize = 50) } returns
            GitHubRepoPage(repos = listOf(kotlin, android), hasNextPage = true)
        val viewModel = createViewModel()
        viewModel.nextPage()

        viewModel.setPageSize(50)

        assertEquals(
            GoogleReposUiState(page = 1, pageSize = 50, repos = listOf(kotlin, android), hasNextPage = true),
            viewModel.uiState.value
        )
    }

    @Test
    fun `page sizes outside the offered options are ignored`() {
        val viewModel = createViewModel()

        viewModel.setPageSize(7)

        assertEquals(10, viewModel.uiState.value.pageSize)
        coVerify(exactly = 0) { repository.getGoogleRepos(any(), pageSize = 7) }
    }

    @Test
    fun `shows the rate limited message on 403 and retry reloads the same page`() {
        coEvery { repository.getGoogleRepos(page = 2, pageSize = 10) } throws
            HttpException(Response.error<Any>(403, "".toResponseBody()))
        val viewModel = createViewModel()
        viewModel.nextPage()

        assertEquals(R.string.error_rate_limited, viewModel.uiState.value.errorMessageRes)

        coEvery { repository.getGoogleRepos(page = 2, pageSize = 10) } returns
            GitHubRepoPage(repos = listOf(android), hasNextPage = false)
        viewModel.retry()

        assertEquals(
            GoogleReposUiState(page = 2, pageSize = 10, repos = listOf(android), hasNextPage = false),
            viewModel.uiState.value
        )
    }

    @Test
    fun `shows a generic message for other failures`() {
        coEvery { repository.getGoogleRepos(page = 1, pageSize = 10) } throws RuntimeException("offline")

        val viewModel = createViewModel()

        assertEquals(R.string.error_repos_unknown, viewModel.uiState.value.errorMessageRes)
    }

    @Test
    fun `page and page size survive the ViewModel being recreated from saved state`() {
        createViewModel().nextPage()

        val recreated = createViewModel()

        assertEquals(2, recreated.uiState.value.page)
        assertEquals(listOf(android), recreated.uiState.value.repos)
    }
}
