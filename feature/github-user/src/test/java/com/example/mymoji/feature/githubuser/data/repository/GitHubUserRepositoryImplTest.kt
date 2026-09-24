package com.example.mymoji.feature.githubuser.data.repository

import com.example.mymoji.core.data.GitHubUserDao
import com.example.mymoji.core.data.GitHubUserEntity
import com.example.mymoji.feature.githubuser.data.remote.GitHubUserApiService
import com.example.mymoji.feature.githubuser.data.remote.GitHubUserResponse
import com.example.mymoji.feature.githubuser.domain.model.GitHubUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GitHubUserRepositoryImplTest {

    private val apiService = mockk<GitHubUserApiService>()
    private val gitHubUserDao = mockk<GitHubUserDao>(relaxUnitFun = true)
    private val repository = GitHubUserRepositoryImpl(apiService, gitHubUserDao)

    private val userEntityMock = GitHubUserEntity(login = "mock", id = 1, avatarUrl = "mock.png")
    private val userMock = GitHubUser(login = "mock", id = 1, avatarUrl = "mock.png")

    @Test
    fun `getUser returns the cached user without calling the api`() = runTest {
        coEvery { gitHubUserDao.getUser("mock") } returns userEntityMock

        assertEquals(userMock, repository.getUser("mock"))
        coVerify(exactly = 0) { apiService.getUser(any()) }
    }

    @Test
    fun `getUser fetches from api and persists on cache miss`() = runTest {
        coEvery { gitHubUserDao.getUser("mock") } returns null
        coEvery { apiService.getUser("mock") } returns
            GitHubUserResponse(login = "mock", id = 1, avatarUrl = "mock.png")

        assertEquals(userMock, repository.getUser("mock"))
        coVerify(exactly = 1) { gitHubUserDao.insert(userEntityMock) }
    }

    @Test
    fun `getUser propagates api failures without persisting`() = runTest {
        coEvery { gitHubUserDao.getUser("test") } returns null
        coEvery { apiService.getUser("test") } throws RuntimeException("404")

        val result = runCatching { repository.getUser("test") }

        assertEquals("404", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { gitHubUserDao.insert(any()) }
    }

    @Test
    fun `getAllUsers returns mapped cached users`() = runTest {
        coEvery { gitHubUserDao.getAllUsers() } returns listOf(userEntityMock)

        assertEquals(listOf(userMock), repository.getAllUsers())
    }

    @Test
    fun `deleteUser removes the user from the cache`() = runTest {
        repository.deleteUser("mock")

        coVerify(exactly = 1) { gitHubUserDao.deleteUser("mock") }
    }

    @Test
    fun `getCachedUser returns the mapped user when present`() = runTest {
        coEvery { gitHubUserDao.getUser("mock") } returns userEntityMock

        assertEquals(userMock, repository.getCachedUser("mock"))
    }

    @Test
    fun `getCachedUser returns null when missing and never calls the api`() = runTest {
        coEvery { gitHubUserDao.getUser("mock") } returns null

        assertNull(repository.getCachedUser("mock"))
        coVerify(exactly = 0) { apiService.getUser(any()) }
    }
}
