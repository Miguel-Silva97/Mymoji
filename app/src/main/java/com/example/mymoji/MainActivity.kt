package com.example.mymoji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mymoji.feature.emoji.presentation.EmojiUiState
import com.example.mymoji.feature.emoji.presentation.EmojiViewModel
import com.example.mymoji.feature.emoji.ui.EmojiListScreen
import com.example.mymoji.feature.githubuser.presentation.GitHubUserListViewModel
import com.example.mymoji.feature.githubuser.presentation.GitHubUserViewModel
import com.example.mymoji.feature.githubuser.ui.GitHubUserListScreen
import com.example.mymoji.feature.googlerepos.presentation.GoogleReposViewModel
import com.example.mymoji.feature.googlerepos.ui.GoogleReposScreen
import com.example.mymoji.presentation.HomeViewModel
import com.example.mymoji.ui.screens.HomeScreen
import com.example.mymoji.ui.theme.MymojiTheme
import dagger.hilt.android.AndroidEntryPoint

private enum class Screen { Home, EmojiList, AvatarList, GoogleRepos }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: EmojiViewModel by viewModels()
    private val gitHubUserViewModel: GitHubUserViewModel by viewModels()
    private val gitHubUserListViewModel: GitHubUserListViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val googleReposViewModel: GoogleReposViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MymojiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()
                    val gitHubUserUiState by gitHubUserViewModel.uiState.collectAsState()
                    val gitHubUserListUiState by gitHubUserListViewModel.uiState.collectAsState()
                    val headerItem by homeViewModel.headerItem.collectAsState()
                    var screen by rememberSaveable { mutableStateOf(Screen.Home) }
                    val screenStateHolder = rememberSaveableStateHolder()

                    screenStateHolder.SaveableStateProvider(screen) {
                        when (screen) {
                            Screen.EmojiList -> {
                                BackHandler { screen = Screen.Home }
                                EmojiListScreen(
                                    emojis = (uiState as? EmojiUiState.Success)?.visibleEmojis.orEmpty(),
                                    onEmojiClick = { emoji -> viewModel.removeEmoji(emoji) },
                                    onRefresh = { viewModel.restoreRemovedEmojis() },
                                    onBackClick = { screen = Screen.Home }
                                )
                            }

                            Screen.AvatarList -> {
                                BackHandler { screen = Screen.Home }
                                GitHubUserListScreen(
                                    uiState = gitHubUserListUiState,
                                    onUserClick = { user ->
                                        gitHubUserListViewModel.deleteUser(user)
                                        gitHubUserViewModel.onUserDeleted(user.login)
                                    },
                                    onBackClick = { screen = Screen.Home }
                                )
                            }

                            Screen.GoogleRepos -> {
                                BackHandler { screen = Screen.Home }
                                val googleReposUiState by googleReposViewModel.uiState.collectAsState()
                                GoogleReposScreen(
                                    uiState = googleReposUiState,
                                    onPageSizeChange = googleReposViewModel::setPageSize,
                                    onPreviousPage = googleReposViewModel::previousPage,
                                    onNextPage = googleReposViewModel::nextPage,
                                    onRetry = googleReposViewModel::retry,
                                    onBackClick = { screen = Screen.Home }
                                )
                            }

                            Screen.Home -> {
                                HomeScreen(
                                    headerItem = headerItem,
                                    uiState = uiState,
                                    gitHubUserUiState = gitHubUserUiState,
                                    onGetEmojiClick = { viewModel.fetchAndPickRandomEmoji() },
                                    onEmojiListClick = {
                                        viewModel.loadEmojis()
                                        screen = Screen.EmojiList
                                    },
                                    onGitHubSearch = { username -> gitHubUserViewModel.searchUser(username) },
                                    onAvatarListClick = {
                                        gitHubUserListViewModel.loadUsers()
                                        screen = Screen.AvatarList
                                    },
                                    onGoogleReposClick = { screen = Screen.GoogleRepos }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
