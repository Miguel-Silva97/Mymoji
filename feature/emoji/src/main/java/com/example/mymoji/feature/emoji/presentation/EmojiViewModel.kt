package com.example.mymoji.feature.emoji.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoji.core.data.LastDisplayedItem
import com.example.mymoji.core.data.LastDisplayedItemStore
import com.example.mymoji.feature.emoji.domain.model.Emoji
import com.example.mymoji.feature.emoji.domain.usecase.GetEmojisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmojiViewModel @Inject constructor(
    private val getEmojisUseCase: GetEmojisUseCase,
    private val lastDisplayedItemStore: LastDisplayedItemStore,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmojiUiState>(EmojiUiState.Idle)
    val uiState: StateFlow<EmojiUiState> = _uiState.asStateFlow()

    private var removedEmojiNames: Set<String>
        get() = savedStateHandle.get<List<String>>(KEY_REMOVED_EMOJIS).orEmpty().toSet()
        set(value) {
            savedStateHandle[KEY_REMOVED_EMOJIS] = ArrayList(value)
        }

    fun fetchAndPickRandomEmoji() {
        viewModelScope.launch {
            loadEmojisIfNeeded() ?: return@launch
            val state = _uiState.value as? EmojiUiState.Success ?: return@launch
            val randomEmoji = state.visibleEmojis.randomOrNull() ?: return@launch
            lastDisplayedItemStore.save(LastDisplayedItem.Emoji(randomEmoji.name))
        }
    }

    fun loadEmojis() {
        viewModelScope.launch { loadEmojisIfNeeded() }
    }

    fun removeEmoji(emoji: Emoji) {
        removedEmojiNames = removedEmojiNames + emoji.name
        _uiState.update { state ->
            (state as? EmojiUiState.Success)?.copy(removedEmojiNames = removedEmojiNames) ?: state
        }
        viewModelScope.launch { lastDisplayedItemStore.removeEmoji(emoji.name) }
    }

    fun restoreRemovedEmojis() {
        removedEmojiNames = emptySet()
        _uiState.update { state ->
            (state as? EmojiUiState.Success)?.copy(removedEmojiNames = emptySet()) ?: state
        }
    }

    private suspend fun loadEmojisIfNeeded(): List<Emoji>? {
        val currentState = _uiState.value
        if (currentState is EmojiUiState.Success) {
            Timber.d("Returning emojies from cache")
            return currentState.emojis
        }

        _uiState.value = EmojiUiState.Loading
        return try {
            val emojiList = getEmojisUseCase()
            _uiState.value = EmojiUiState.Success(emojis = emojiList, removedEmojiNames = removedEmojiNames)
            emojiList
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.value = EmojiUiState.Error(message = e.localizedMessage ?: "Unknown Error occurred")
            null
        }
    }

    private companion object {
        const val KEY_REMOVED_EMOJIS = "removed_emoji_names"
    }
}
