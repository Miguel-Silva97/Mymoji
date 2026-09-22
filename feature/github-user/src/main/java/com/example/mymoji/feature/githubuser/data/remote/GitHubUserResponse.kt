package com.example.mymoji.feature.githubuser.data.remote

import com.google.gson.annotations.SerializedName

data class GitHubUserResponse(
    val login: String,
    val id: Long,
    @SerializedName("avatar_url") val avatarUrl: String
)
