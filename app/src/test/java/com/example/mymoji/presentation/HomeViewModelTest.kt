package com.example.mymoji.presentation

import com.example.mymoji.core.data.LastDisplayedItem
import com.example.mymoji.core.data.LastDisplayedItemStore
import com.example.mymoji.feature.emoji.domain.model.Emoji
import com.example.mymoji.feature.emoji.domain.repository.EmojiRepository
import com.example.mymoji.feature.emoji.domain.usecase.GetCachedEmojiUseCase
import com.example.mymoji.feature.githubuser.domain.model.GitHubUser
import com.example.mymoji.feature.githubuser.domain.repository.GitHubUserRepository
import com.example.mymoji.feature.githubuser.domain.usecase.GetCachedGitHubUserUseCase
import com.example.mymoji.speech.TextSpeaker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val history = MutableStateFlow<List<LastDisplayedItem>>(emptyList())
    private val lastDisplayedItemStore = mockk<LastDisplayedItemStore> {
        every { history } returns this@HomeViewModelTest.history
    }
    private val emojiRepository = mockk<EmojiRepository> {
        coEvery { getCachedEmoji(any()) } returns null
    }
    private val gitHubUserRepository = mockk<GitHubUserRepository> {
        coEvery { getCachedUser(any()) } returns null
    }

    private val textSpeaker = mockk<TextSpeaker>(relaxed = true)

    private val smile = Emoji("smile", "smile.png")
    private val sweatSmile = Emoji("sad", "sad.png")
    private val wink = Emoji("wink", "wink.png")
    private val mockUSer = GitHubUser(login = "mockUSer", id = 1, avatarUrl = "mockUSer.png")

    private fun createViewModel() = HomeViewModel(
        lastDisplayedItemStore = lastDisplayedItemStore,
        getCachedEmojiUseCase = GetCachedEmojiUseCase(emojiRepository),
        getCachedGitHubUserUseCase = GetCachedGitHubUserUseCase(gitHubUserRepository),
        textSpeaker = textSpeaker
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        coEvery { emojiRepository.getCachedEmoji("smile") } returns smile
        coEvery { emojiRepository.getCachedEmoji("wink") } returns wink
        coEvery { emojiRepository.getCachedEmoji("sad") } returns sweatSmile
        coEvery { gitHubUserRepository.getCachedUser("mockUSer") } returns mockUSer
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun TestScope.collect(viewModel: HomeViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.headerItem.collect {} }
    }

    @Test
    fun `header is empty when nothing was displayed`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel)

        assertNull(viewModel.headerItem.value)
    }

    @Test
    fun `header shows the most recently displayed item`() = runTest {
        history.value = listOf(LastDisplayedItem.GitHubUser("mockUSer"), LastDisplayedItem.Emoji("smile"))
        val viewModel = createViewModel()
        collect(viewModel)

        assertEquals(HeaderItem.AvatarItem(mockUSer), viewModel.headerItem.value)
    }

    @Test
    fun `header goes back to the previous item when the current one is removed`() = runTest {
        history.value = listOf(LastDisplayedItem.Emoji("wink"), LastDisplayedItem.Emoji("smile"))
        val viewModel = createViewModel()
        collect(viewModel)

        history.value = listOf(LastDisplayedItem.Emoji("smile"))

        assertEquals(HeaderItem.EmojiItem(smile), viewModel.headerItem.value)
    }

    @Test
    fun `header skips items that are no longer cached`() = runTest {
        history.value = listOf(LastDisplayedItem.GitHubUser("fakeuser"), LastDisplayedItem.Emoji("smile"))
        val viewModel = createViewModel()
        collect(viewModel)

        assertEquals(HeaderItem.EmojiItem(smile), viewModel.headerItem.value)
    }

    @Test
    fun `speakHeaderItem reads the emoji name with spaces instead of underscores`() = runTest {
        history.value = listOf(LastDisplayedItem.Emoji("sad"))
        val viewModel = createViewModel()
        collect(viewModel)

        viewModel.speakHeaderItem()

        verify(exactly = 1) { textSpeaker.speak("sad") }
    }

    @Test
    fun `speakHeaderItem reads the GitHub username`() = runTest {
        history.value = listOf(LastDisplayedItem.GitHubUser("mockUSer"))
        val viewModel = createViewModel()
        collect(viewModel)

        viewModel.speakHeaderItem()

        verify(exactly = 1) { textSpeaker.speak("mockUSer") }
    }

    @Test
    fun `speakHeaderItem says nothing when the header is empty`() = runTest {
        val viewModel = createViewModel()
        collect(viewModel)

        viewModel.speakHeaderItem()

        verify(exactly = 0) { textSpeaker.speak(any()) }
    }
}
