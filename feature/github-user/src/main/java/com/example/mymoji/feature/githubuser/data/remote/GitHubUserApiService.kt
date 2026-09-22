package com.example.mymoji.feature.githubuser.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubUserApiService {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GitHubUserResponse
}
