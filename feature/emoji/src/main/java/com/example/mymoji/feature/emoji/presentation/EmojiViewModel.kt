package com.example.mymoji.feature.emoji.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoji.core.data.LastDisplayedItem
import com.example.mymoji.core.data.LastDisplayedItemStore
import com.example.mymoji.feature.emoji.domain.model.Emoji
import com.example.mymoji.feature.emoji.domain.usecase.GetCachedEmojiUseCase
import com.example.mymoji.feature.emoji.domain.usecase.GetEmojisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmojiViewModel @Inject constructor(
    private val getEmojisUseCase: GetEmojisUseCase,
    private val getCachedEmojiUseCase: GetCachedEmojiUseCase,
    private val lastDisplayedItemStore: LastDisplayedItemStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmojiUiState>(EmojiUiState.Idle)
    val uiState: StateFlow<EmojiUiState> = _uiState.asStateFlow()

    init {
        restoreLastDisplayedEmoji()
    }

    private fun restoreLastDisplayedEmoji() {
        viewModelScope.launch {
            val lastEmojiName = (lastDisplayedItemStore.load() as? LastDisplayedItem.Emoji)?.name ?: return@launch
            val emoji = getCachedEmojiUseCase(lastEmojiName) ?: return@launch
            _uiState.value = EmojiUiState.Success(emojis = emptyList(), currentRandomEmoji = emoji)
        }
    }

    fun fetchAndPickRandomEmoji() {
        viewModelScope.launch {
            val emojis = loadEmojisIfNeeded() ?: return@launch
            val randomEmoji = emojis.random()
            _uiState.update { state ->
                (state as? EmojiUiState.Success)?.copy(currentRandomEmoji = randomEmoji) ?: state
            }
            lastDisplayedItemStore.save(LastDisplayedItem.Emoji(randomEmoji.name))
        }
    }

    fun loadEmojis() {
        viewModelScope.launch { loadEmojisIfNeeded() }
    }

    private suspend fun loadEmojisIfNeeded(): List<Emoji>? {
        val currentState = _uiState.value
        if (currentState is EmojiUiState.Success && currentState.emojis.isNotEmpty()) {
            return currentState.emojis
        }

        val previousRandomEmoji = (currentState as? EmojiUiState.Success)?.currentRandomEmoji
        _uiState.value = EmojiUiState.Loading
        return try {
            val emojiList = getEmojisUseCase()
            _uiState.value = EmojiUiState.Success(emojis = emojiList, currentRandomEmoji = previousRandomEmoji)
            emojiList
        } catch (e: Exception) {
            _uiState.value = EmojiUiState.Error(message = e.localizedMessage ?: "Unknown Error occurred")
            null
        }
    }
}
