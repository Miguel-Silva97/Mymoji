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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mymoji.feature.emoji.presentation.EmojiUiState
import com.example.mymoji.feature.emoji.presentation.EmojiViewModel
import com.example.mymoji.feature.emoji.ui.EmojiListScreen
import com.example.mymoji.feature.githubuser.presentation.GitHubUserViewModel
import com.example.mymoji.ui.screens.HomeScreen
import com.example.mymoji.ui.theme.MymojiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: EmojiViewModel by viewModels()
    private val gitHubUserViewModel: GitHubUserViewModel by viewModels()

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
                    var showEmojiList by remember { mutableStateOf(false) }

                    if (showEmojiList) {
                        BackHandler { showEmojiList = false }
                        EmojiListScreen(
                            emojis = (uiState as? EmojiUiState.Success)?.emojis.orEmpty(),
                            onBackClick = { showEmojiList = false }
                        )
                    } else {
                        HomeScreen(
                            uiState = uiState,
                            gitHubUserUiState = gitHubUserUiState,
                            onGetEmojiClick = { viewModel.fetchAndPickRandomEmoji() },
                            onEmojiListClick = { showEmojiList = true },
                            onGitHubSearch = { username -> gitHubUserViewModel.searchUser(username) }
                        )
                    }
                }
            }
        }
    }
}
