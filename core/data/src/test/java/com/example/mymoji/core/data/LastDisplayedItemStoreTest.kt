package com.example.mymoji.core.data

import androidx.datastore.core.DataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class LastDisplayedItemStoreTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val smile = LastDisplayedItem.Emoji("smile")
    private val wink = LastDisplayedItem.Emoji("wink")
    private val mockUser = LastDisplayedItem.GitHubUser("mockUser")

    private fun TestScope.createStore() = LastDisplayedItemStore(
        DataStoreFactory.create(DisplayHistorySerializer, scope = backgroundScope) { tempFolder.newFile("test.pb") }
    )

    private suspend fun LastDisplayedItemStore.currentHistory() = history.first()

    @Test
    fun `history is empty when nothing was saved`() = runTest {
        assertEquals(emptyList<LastDisplayedItem>(), createStore().currentHistory())
    }

    @Test
    fun `history lists saved items most recent first`() = runTest {
        val store = createStore()
        store.save(smile)
        store.save(mockUser)
        store.save(wink)

        assertEquals(listOf(wink, mockUser, smile), store.currentHistory())
    }

    @Test
    fun `saving an item again moves it to the front instead of duplicating it`() = runTest {
        val store = createStore()
        store.save(smile)
        store.save(wink)
        store.save(smile)

        assertEquals(listOf(smile, wink), store.currentHistory())
    }

    @Test
    fun `history is capped to the most recent 10 items`() = runTest {
        val store = createStore()
        repeat(15) { store.save(LastDisplayedItem.Emoji("emoji$it")) }

        val history = store.currentHistory()
        assertEquals(10, history.size)
        assertEquals(LastDisplayedItem.Emoji("emoji14"), history.first())
    }

    @Test
    fun `removeEmoji makes the previously displayed item current again`() = runTest {
        val store = createStore()
        store.save(smile)
        store.save(wink)

        store.removeEmoji("wink")

        assertEquals(listOf(smile), store.currentHistory())
    }

    @Test
    fun `removeGitHubUser removes the user from anywhere in the history`() = runTest {
        val store = createStore()
        store.save(mockUser)
        store.save(smile)

        store.removeGitHubUser("mockUser")

        assertEquals(listOf(smile), store.currentHistory())
    }
}
