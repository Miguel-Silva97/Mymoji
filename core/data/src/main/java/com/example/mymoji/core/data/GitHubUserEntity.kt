package com.example.mymoji.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.annotation.concurrent.Immutable

@Entity(tableName = "github_users")
@Immutable
data class GitHubUserEntity(
    @PrimaryKey val login: String,
    val id: Long,
    val avatarUrl: String
)
