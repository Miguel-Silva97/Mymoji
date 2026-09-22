package com.example.mymoji.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "github_users")
data class GitHubUserEntity(
    @PrimaryKey val login: String,
    val id: Long,
    val avatarUrl: String
)
