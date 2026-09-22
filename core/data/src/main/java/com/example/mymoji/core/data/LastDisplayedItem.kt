package com.example.mymoji.core.data

sealed interface LastDisplayedItem {
    data class Emoji(val name: String) : LastDisplayedItem
    data class GitHubUser(val login: String) : LastDisplayedItem
}
