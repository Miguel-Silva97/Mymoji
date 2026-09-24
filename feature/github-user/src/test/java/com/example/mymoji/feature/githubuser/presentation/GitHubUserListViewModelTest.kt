package com.example.mymoji.feature.githubuser.presentation

import com.example.mymoji.feature.githubuser.domain.model.GitHubUser
import com.example.mymoji.feature.githubuser.domain.repository.GitHubUserRepository
import com.example.mymoji.feature.githubuser.domain.usecase.DeleteGitHubUserUseCase
import com.example.mymoji.feature.githubuser.domain.usecase.GetGitHubUsersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubUserListViewModelTest {

    private val repository = mockk<GitHubUserRepository>(relaxUnitFun = true)

    private val mockUser = GitHubUser(login = "mockUser", id = 1, avatarUrl = "mockUser.png")
    private val mockSecondUser = GitHubUser(login = "mockSecondUser", id = 2, avatarUrl = "mockSecondUser.png")

    private fun createViewModel() = GitHubUserListViewModel(
        getGitHubUsersUseCase = GetGitHubUsersUseCase(repository),
        deleteGitHubUserUseCase = DeleteGitHubUserUseCase(repository)
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
        assertEquals(GitHubUserListUiState.Idle, createViewModel().uiState.value)
    }

    @Test
    fun `loadUsers emits Success with the cached users`() {
        coEvery { repository.getAllUsers() } returns listOf(mockSecondUser, mockUser)
        val viewModel = createViewModel()

        viewModel.loadUsers()

        assertEquals(GitHubUserListUiState.Success(listOf(mockSecondUser, mockUser)), viewModel.uiState.value)
    }

    @Test
    fun `loadUsers emits Error on failure`() {
        coEvery { repository.getAllUsers() } throws RuntimeException("DB error")
        val viewModel = createViewModel()

        viewModel.loadUsers()

        assertEquals(GitHubUserListUiState.Error("DB error"), viewModel.uiState.value)
    }

    @Test
    fun `loadUsers emits a fallback Error message when the exception has none`() {
        coEvery { repository.getAllUsers() } throws RuntimeException()
        val viewModel = createViewModel()

        viewModel.loadUsers()

        assertEquals(GitHubUserListUiState.Error("Unknown Error occurred"), viewModel.uiState.value)
    }

    @Test
    fun `deleteUser removes the user from storage and from the list`() = runTest {
        coEvery { repository.getAllUsers() } returns listOf(mockSecondUser, mockUser)
        val viewModel = createViewModel()
        viewModel.loadUsers()

        viewModel.deleteUser(mockUser)

        assertEquals(GitHubUserListUiState.Success(listOf(mockSecondUser)), viewModel.uiState.value)
        coVerify(exactly = 1) { repository.deleteUser("mockUser") }
    }

    @Test
    fun `deleteUser does nothing when users have not been loaded`() = runTest {
        val viewModel = createViewModel()

        viewModel.deleteUser(mockUser)

        assertEquals(GitHubUserListUiState.Idle, viewModel.uiState.value)
        coVerify(exactly = 0) { repository.deleteUser(any()) }
    }
}
