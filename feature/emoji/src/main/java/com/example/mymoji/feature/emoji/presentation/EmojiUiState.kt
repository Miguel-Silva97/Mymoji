package com.example.mymoji.feature.emoji.presentation

import com.example.mymoji.feature.emoji.domain.model.Emoji

sealed interface EmojiUiState {
    object Idle : EmojiUiState
    object Loading : EmojiUiState
    data class Success(val emojis: List<Emoji>, val currentRandomEmoji: Emoji?) : EmojiUiState
    data class Error(val message: String) : EmojiUiState
}
