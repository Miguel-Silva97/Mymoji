package com.example.mymoji.feature.googlerepos.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubRepoApiService {
    @GET("users/google/repos")
    suspend fun getGoogleRepos(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int
    ): Response<List<GitHubRepoResponse>>
}
