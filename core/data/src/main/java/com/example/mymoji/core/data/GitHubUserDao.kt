package com.example.mymoji.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GitHubUserDao {

    // GitHub usernames are case-insensitive, so match regardless of case.
    @Query("SELECT * FROM github_users WHERE login = :login COLLATE NOCASE LIMIT 1")
    suspend fun getUser(login: String): GitHubUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: GitHubUserEntity)
}
