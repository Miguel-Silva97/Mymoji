package com.example.mymoji.feature.googlerepos.data.repository

import com.example.mymoji.feature.googlerepos.data.remote.GitHubRepoApiService
import com.example.mymoji.feature.googlerepos.domain.model.GitHubRepo
import com.example.mymoji.feature.googlerepos.domain.model.GitHubRepoPage
import com.example.mymoji.feature.googlerepos.domain.repository.GitHubRepoRepository
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepoRepositoryImpl @Inject constructor(
    private val apiService: GitHubRepoApiService
) : GitHubRepoRepository {

    override suspend fun getGoogleRepos(page: Int, pageSize: Int): GitHubRepoPage {
        val response = apiService.getGoogleRepos(page = page, pageSize = pageSize)
        Timber.d("Response for google repos was: ${response.code()}")
        if (!response.isSuccessful) throw HttpException(response)

        val repos = response.body().orEmpty().map { GitHubRepo(name = it.name, description = it.description) }
        val hasNextPage = response.headers()["Link"]?.contains("rel=\"next\"") == true
        return GitHubRepoPage(repos = repos, hasNextPage = hasNextPage)
    }
}
