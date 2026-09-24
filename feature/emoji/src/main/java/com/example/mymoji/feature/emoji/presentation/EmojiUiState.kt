package com.example.mymoji.feature.emoji.presentation

import com.example.mymoji.feature.emoji.domain.model.Emoji

sealed interface EmojiUiState {
    object Idle : EmojiUiState
    object Loading : EmojiUiState
    data class Success(
        val emojis: List<Emoji>,
        val removedEmojiNames: Set<String> = emptySet()
    ) : EmojiUiState {
        val visibleEmojis: List<Emoji>
            get() = emojis.filterNot { it.name in removedEmojiNames }
    }
    data class Error(val message: String) : EmojiUiState
}
