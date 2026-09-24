package com.example.mymoji.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymoji.core.data.LastDisplayedItem
import com.example.mymoji.core.data.LastDisplayedItemStore
import com.example.mymoji.feature.emoji.domain.model.Emoji
import com.example.mymoji.feature.emoji.domain.usecase.GetCachedEmojiUseCase
import com.example.mymoji.feature.githubuser.domain.model.GitHubUser
import com.example.mymoji.feature.githubuser.domain.usecase.GetCachedGitHubUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@Immutable
sealed interface HeaderItem {
    data class EmojiItem(val emoji: Emoji) : HeaderItem
    data class AvatarItem(val user: GitHubUser) : HeaderItem
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    lastDisplayedItemStore: LastDisplayedItemStore,
    private val getCachedEmojiUseCase: GetCachedEmojiUseCase,
    private val getCachedGitHubUserUseCase: GetCachedGitHubUserUseCase
) : ViewModel() {

    val headerItem: StateFlow<HeaderItem?> = lastDisplayedItemStore.history
        .map { history -> history.firstNotNullOfOrNull { resolve(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), null)

    private suspend fun resolve(item: LastDisplayedItem): HeaderItem? = when (item) {
        is LastDisplayedItem.Emoji -> getCachedEmojiUseCase(item.name)?.let(HeaderItem::EmojiItem)
        is LastDisplayedItem.GitHubUser -> getCachedGitHubUserUseCase(item.login)?.let(HeaderItem::AvatarItem)
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
