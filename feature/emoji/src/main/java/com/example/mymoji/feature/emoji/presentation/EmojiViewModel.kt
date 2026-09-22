package com.example.mymoji.feature.emoji.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val getEmojisUseCase: GetEmojisUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmojiUiState>(EmojiUiState.Idle)
    val uiState: StateFlow<EmojiUiState> = _uiState.asStateFlow()

    fun fetchAndPickRandomEmoji() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is EmojiUiState.Success) {
                // If the cache is already fully loaded into presentation state, pick directly from memory
                if (currentState.emojis.isNotEmpty()) {
                    val randomEmoji = currentState.emojis.random()
                    _uiState.update { currentState.copy(currentRandomEmoji = randomEmoji) }
                    return@launch
                }
            }

            // Otherwise load/pull into cache safely
            _uiState.value = EmojiUiState.Loading
            try {
                val emojiList = getEmojisUseCase()
                val randomEmoji = if (emojiList.isNotEmpty()) emojiList.random() else null
                _uiState.value = EmojiUiState.Success(emojis = emojiList, currentRandomEmoji = randomEmoji)
            } catch (e: Exception) {
                _uiState.value = EmojiUiState.Error(message = e.localizedMessage ?: "Unknown Error occurred")
            }
        }
    }
}
