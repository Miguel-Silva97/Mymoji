package com.example.mymoji.data.remote

import retrofit2.http.GET

interface GitHubApiService {
    @GET("emojis")
    suspend fun getEmojis(): Map<String, String>
}
