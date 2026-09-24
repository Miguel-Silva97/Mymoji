package com.example.mymoji.feature.emoji.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.mymoji.core.data.LastDisplayedItem
import com.example.mymoji.core.data.LastDisplayedItemStore
import com.example.mymoji.feature.emoji.domain.model.Emoji
import com.example.mymoji.feature.emoji.domain.repository.EmojiRepository
import com.example.mymoji.feature.emoji.domain.usecase.GetEmojisUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EmojiViewModelTest {

    private val repository = mockk<EmojiRepository>()
    private val lastDisplayedItemStore = mockk<LastDisplayedItemStore>(relaxUnitFun = true)

    private val savedStateHandle = SavedStateHandle()

    private val smile = Emoji("smile", "smile.png")
    private val wink = Emoji("wink", "wink.png")

    private fun createViewModel() = EmojiViewModel(
        getEmojisUseCase = GetEmojisUseCase(repository),
        lastDisplayedItemStore = lastDisplayedItemStore,
        savedStateHandle = savedStateHandle
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(EmojiUiState.Idle, createViewModel().uiState.value)
    }

    @Test
    fun `loadEmojis emits Success with the fetched list`() {
        coEvery { repository.getEmojis() } returns listOf(smile, wink)
        val viewModel = createViewModel()

        viewModel.loadEmojis()

        assertEquals(EmojiUiState.Success(emojis = listOf(smile, wink)), viewModel.uiState.value)
    }

    @Test
    fun `loadEmojis does not refetch once emojis are loaded`() {
        coEvery { repository.getEmojis() } returns listOf(smile)
        val viewModel = createViewModel()

        viewModel.loadEmojis()
        viewModel.loadEmojis()

        coVerify(exactly = 1) { repository.getEmojis() }
    }

    @Test
    fun `loadEmojis emits Error with the exception message on failure`() {
        coEvery { repository.getEmojis() } throws RuntimeException("Network down")
        val viewModel = createViewModel()

        viewModel.loadEmojis()

        assertEquals(EmojiUiState.Error("Network down"), viewModel.uiState.value)
    }

    @Test
    fun `loadEmojis emits a fallback Error message when the exception has none`() {
        coEvery { repository.getEmojis() } throws RuntimeException()
        val viewModel = createViewModel()

        viewModel.loadEmojis()

        assertEquals(EmojiUiState.Error("Unknown Error occurred"), viewModel.uiState.value)
    }

    @Test
    fun `fetchAndPickRandomEmoji saves the picked emoji to the display history`() {
        coEvery { repository.getEmojis() } returns listOf(smile)
        val viewModel = createViewModel()

        viewModel.fetchAndPickRandomEmoji()

        coVerify(exactly = 1) { lastDisplayedItemStore.save(LastDisplayedItem.Emoji("smile")) }
    }

    @Test
    fun `fetchAndPickRandomEmoji does not save anything on failure`() {
        coEvery { repository.getEmojis() } throws RuntimeException("Network down")
        val viewModel = createViewModel()

        viewModel.fetchAndPickRandomEmoji()

        assertEquals(EmojiUiState.Error("Network down"), viewModel.uiState.value)
        coVerify(exactly = 0) { lastDisplayedItemStore.save(any()) }
    }

    @Test
    fun `fetchAndPickRandomEmoji never picks a removed emoji`() {
        coEvery { repository.getEmojis() } returns listOf(smile, wink)
        val viewModel = createViewModel()
        viewModel.loadEmojis()
        viewModel.removeEmoji(wink)

        repeat(20) { viewModel.fetchAndPickRandomEmoji() }

        coVerify(exactly = 20) { lastDisplayedItemStore.save(LastDisplayedItem.Emoji("smile")) }
        coVerify(exactly = 0) { lastDisplayedItemStore.save(LastDisplayedItem.Emoji("wink")) }
    }

    @Test
    fun `removeEmoji hides the emoji and drops it from the display history`() {
        coEvery { repository.getEmojis() } returns listOf(smile, wink)
        val viewModel = createViewModel()
        viewModel.loadEmojis()

        viewModel.removeEmoji(wink)

        assertEquals(listOf(smile), (viewModel.uiState.value as EmojiUiState.Success).visibleEmojis)
        coVerify(exactly = 1) { lastDisplayedItemStore.removeEmoji("wink") }
    }

    @Test
    fun `restoreRemovedEmojis brings removed emojis back`() {
        coEvery { repository.getEmojis() } returns listOf(smile, wink)
        val viewModel = createViewModel()
        viewModel.loadEmojis()
        viewModel.removeEmoji(wink)

        viewModel.restoreRemovedEmojis()

        assertEquals(listOf(smile, wink), (viewModel.uiState.value as EmojiUiState.Success).visibleEmojis)
    }

    @Test
    fun `removed emojis survive the ViewModel being recreated from saved state`() {
        coEvery { repository.getEmojis() } returns listOf(smile, wink)
        createViewModel().apply {
            loadEmojis()
            removeEmoji(wink)
        }

        val recreated = createViewModel()
        recreated.loadEmojis()

        assertEquals(listOf(smile), (recreated.uiState.value as EmojiUiState.Success).visibleEmojis)
    }
}
