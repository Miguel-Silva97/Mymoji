package com.example.mymoji.core.data

import androidx.datastore.core.DataStore
import com.example.mymoji.core.data.proto.DisplayHistory
import com.example.mymoji.core.data.proto.DisplayedItem
import com.example.mymoji.core.data.proto.displayHistory
import com.example.mymoji.core.data.proto.displayedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LastDisplayedItemStore @Inject constructor(
    private val dataStore: DataStore<DisplayHistory>
) {

    val history: Flow<List<LastDisplayedItem>> = dataStore.data.map { it.toDomain() }

    suspend fun save(item: LastDisplayedItem) = editHistory { history ->
        (listOf(item) + history.filterNot { it == item }).take(MAX_HISTORY_SIZE)
    }

    suspend fun removeEmoji(name: String) = remove(LastDisplayedItem.Emoji(name))

    suspend fun removeGitHubUser(login: String) = remove(LastDisplayedItem.GitHubUser(login))

    private suspend fun remove(item: LastDisplayedItem) = editHistory { history ->
        history.filterNot { it == item }
    }

    private suspend fun editHistory(transform: (List<LastDisplayedItem>) -> List<LastDisplayedItem>) {
        dataStore.updateData { stored ->
            displayHistory { items += transform(stored.toDomain()).map { it.toProto() } }
        }
    }

    private fun DisplayHistory.toDomain(): List<LastDisplayedItem> = itemsList.mapNotNull { item ->
        when (item.itemCase) {
            DisplayedItem.ItemCase.EMOJI_NAME -> LastDisplayedItem.Emoji(item.emojiName)
            DisplayedItem.ItemCase.GITHUB_LOGIN -> LastDisplayedItem.GitHubUser(item.githubLogin)
            DisplayedItem.ItemCase.ITEM_NOT_SET, null -> null
        }
    }

    private fun LastDisplayedItem.toProto(): DisplayedItem = displayedItem {
        when (val item = this@toProto) {
            is LastDisplayedItem.Emoji -> emojiName = item.name
            is LastDisplayedItem.GitHubUser -> githubLogin = item.login
        }
    }

    private companion object {
        const val MAX_HISTORY_SIZE = 10
    }
}
