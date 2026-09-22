package com.example.mymoji.core.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GitHubUserDao {

    @Query("SELECT * FROM github_users WHERE login = :login LIMIT 1")
    suspend fun getUser(login: String): GitHubUserEntity?

    @Query("SELECT * FROM github_users ORDER BY login ASC")
    suspend fun getAllUsers(): List<GitHubUserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: GitHubUserEntity)

    @Query("DELETE FROM github_users WHERE login = :login")
    suspend fun deleteUser(login: String)
}
