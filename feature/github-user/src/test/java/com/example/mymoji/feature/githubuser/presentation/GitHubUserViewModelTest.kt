package com.example.mymoji.feature.githubuser.presentation

import com.example.mymoji.core.data.LastDisplayedItem
import com.example.mymoji.core.data.LastDisplayedItemStore
import com.example.mymoji.feature.githubuser.R
import com.example.mymoji.feature.githubuser.domain.model.GitHubUser
import com.example.mymoji.feature.githubuser.domain.repository.GitHubUserRepository
import com.example.mymoji.feature.githubuser.domain.usecase.GetGitHubUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubUserViewModelTest {

    private val repository = mockk<GitHubUserRepository>()
    private val lastDisplayedItemStore = mockk<LastDisplayedItemStore>(relaxUnitFun = true)

    private val mockUser = GitHubUser(login = "mockuser", id = 1, avatarUrl = "mockUser.png")

    private fun createViewModel() = GitHubUserViewModel(
        getGitHubUserUseCase = GetGitHubUserUseCase(repository),
        lastDisplayedItemStore = lastDisplayedItemStore
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        val viewModel = createViewModel()

        assertEquals(GitHubUserUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `searchUser ignores blank input`() {
        val viewModel = createViewModel()

        viewModel.searchUser("   ")

        assertEquals(GitHubUserUiState.Idle, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.getUser(any()) }
    }

    @Test
    fun `searchUser trims the username, emits Success and persists the returned login`() = runTest {
        coEvery { repository.getUser("MockUser") } returns mockUser
        val viewModel = createViewModel()

        viewModel.searchUser("  MockUser  ")

        assertEquals(GitHubUserUiState.Success(mockUser), viewModel.uiState.value)
        coVerify(exactly = 1) { lastDisplayedItemStore.save(LastDisplayedItem.GitHubUser("mockuser")) }
    }

    @Test
    fun `searchUser emits a generic Error and persists nothing on non-HTTP failure`() = runTest {
        coEvery { repository.getUser("fake") } throws RuntimeException("error")
        val viewModel = createViewModel()

        viewModel.searchUser("fake")

        assertEquals(GitHubUserUiState.Error(R.string.error_unknown), viewModel.uiState.value)
        coVerify(exactly = 0) { lastDisplayedItemStore.save(any()) }
    }

    @Test
    fun `searchUser emits a generic Error on other HTTP codes`() {
        coEvery { repository.getUser("fake") } throws httpException(500)
        val viewModel = createViewModel()

        viewModel.searchUser("fake")

        assertEquals(GitHubUserUiState.Error(R.string.error_unknown), viewModel.uiState.value)
    }

    @Test
    fun `searchUser emits the rate limited message on 403`() {
        coEvery { repository.getUser("mockuser") } returns mockUser
        coEvery { repository.getUser("fake") } throws httpException(403)
        val viewModel = createViewModel()
        viewModel.searchUser("mockuser")

        viewModel.searchUser("fake")

        assertEquals(
            GitHubUserUiState.Error(R.string.error_rate_limited),
            viewModel.uiState.value
        )
    }

    @Test
    fun `searchUser emits the not found message on 404`() {
        coEvery { repository.getUser("ghost") } throws httpException(404)
        val viewModel = createViewModel()

        viewModel.searchUser("ghost")

        assertEquals(
            GitHubUserUiState.Error(R.string.error_user_not_found),
            viewModel.uiState.value
        )
    }

    @Test
    fun `onUserDeleted removes the user from the display history`() {
        val viewModel = createViewModel()

        viewModel.onUserDeleted("octocat")

        coVerify(exactly = 1) { lastDisplayedItemStore.removeGitHubUser("octocat") }
    }

    private fun httpException(code: Int) =
        HttpException(Response.error<Any>(code, "".toResponseBody()))
}
