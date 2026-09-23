package com.example.mymoji.feature.emoji.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Emoji(
    val name: String,
    val url: String
)
