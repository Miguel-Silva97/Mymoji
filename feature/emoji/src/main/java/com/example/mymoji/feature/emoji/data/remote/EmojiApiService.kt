package com.example.mymoji.feature.emoji.data.remote

import retrofit2.http.GET

interface EmojiApiService {
    @GET("emojis")
    suspend fun getEmojis(): Map<String, String>
}
