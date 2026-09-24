package com.example.mymoji.feature.googlerepos.data.repository

import com.example.mymoji.feature.googlerepos.data.remote.GitHubRepoApiService
import com.example.mymoji.feature.googlerepos.data.remote.GitHubRepoResponse
import com.example.mymoji.feature.googlerepos.domain.model.GitHubRepo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.Headers.Companion.headersOf
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class GitHubRepoRepositoryImplTest {

    private val apiService = mockk<GitHubRepoApiService>()
    private val repository = GitHubRepoRepositoryImpl(apiService)

    private val kotlin = GitHubRepoResponse(name = "kotlin", description = "Kotlin desc")
    private val gson = GitHubRepoResponse(name = "gson", description = null)

    private fun linkHeader(vararg rels: String) = headersOf(
        "Link",
        rels.joinToString(", ") { "<https://api.github.com/user/1/repos?page=2&per_page=10>; rel=\"$it\"" }
    )

    @Test
    fun `requests the given page and page size and maps name and description`() = runTest {
        coEvery { apiService.getGoogleRepos(page = 2, pageSize = 5) } returns Response.success(listOf(kotlin, gson))

        val result = repository.getGoogleRepos(page = 2, pageSize = 5)

        assertEquals(
            listOf(
                GitHubRepo(name = "kotlin", description = "Kotlin desc"),
                GitHubRepo(name = "gson", description = null)
            ),
            result.repos
        )
    }

    @Test
    fun `has a next page when the Link header lists one`() = runTest {
        coEvery { apiService.getGoogleRepos(any(), any()) } returns
            Response.success(listOf(kotlin), linkHeader("prev", "next", "last", "first"))

        assertTrue(repository.getGoogleRepos(page = 2, pageSize = 10).hasNextPage)
    }

    @Test
    fun `has no next page on the last page`() = runTest {
        coEvery { apiService.getGoogleRepos(any(), any()) } returns
            Response.success(listOf(kotlin), linkHeader("prev", "first"))

        assertFalse(repository.getGoogleRepos(page = 5, pageSize = 10).hasNextPage)
    }

    @Test
    fun `has no next page when everything fits on one page`() = runTest {
        coEvery { apiService.getGoogleRepos(any(), any()) } returns Response.success(listOf(kotlin))

        assertFalse(repository.getGoogleRepos(page = 1, pageSize = 10).hasNextPage)
    }

    @Test
    fun `throws an HttpException for error responses`() = runTest {
        coEvery { apiService.getGoogleRepos(any(), any()) } returns Response.error(403, "".toResponseBody())

        val error = runCatching { repository.getGoogleRepos(page = 1, pageSize = 10) }.exceptionOrNull()

        assertEquals(403, (error as HttpException).code())
    }
}
