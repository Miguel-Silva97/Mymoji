package com.example.mymoji.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mymoji.R
import com.example.mymoji.feature.emoji.presentation.EmojiUiState
import com.example.mymoji.feature.githubuser.presentation.GitHubUserUiState
import com.example.mymoji.feature.githubuser.ui.GitHubUserSearch

// Tracks which of the two features most recently produced a result, so the
// header box below can show whichever one the user last asked for.
private enum class ActiveHeader { Emoji, GitHubUser }

@Composable
fun HomeScreen(
    uiState: EmojiUiState = EmojiUiState.Idle,
    gitHubUserUiState: GitHubUserUiState = GitHubUserUiState.Idle,
    onGetEmojiClick: () -> Unit = {},
    onEmojiListClick: () -> Unit = {},
    onGitHubSearch: (String) -> Unit = {},
    onAvatarListClick: () -> Unit = {},
    onGoogleReposClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var activeHeader by remember { mutableStateOf(ActiveHeader.Emoji) }
    var headerLocked by remember { mutableStateOf(false) }

    LaunchedEffect(uiState, gitHubUserUiState) {
        if (headerLocked) return@LaunchedEffect
        when {
            gitHubUserUiState is GitHubUserUiState.Success -> {
                activeHeader = ActiveHeader.GitHubUser
                headerLocked = true
            }
            uiState is EmojiUiState.Success && uiState.currentRandomEmoji != null -> {
                activeHeader = ActiveHeader.Emoji
                headerLocked = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        val fetchedEmoji = (uiState as? EmojiUiState.Success)?.currentRandomEmoji
        val fetchedUser = (gitHubUserUiState as? GitHubUserUiState.Success)?.user
        Box(
            modifier = Modifier.size(96.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                activeHeader == ActiveHeader.Emoji && uiState is EmojiUiState.Loading -> CircularProgressIndicator()
                activeHeader == ActiveHeader.Emoji && fetchedEmoji != null -> AsyncImage(
                    model = fetchedEmoji.url,
                    contentDescription = fetchedEmoji.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                activeHeader == ActiveHeader.GitHubUser && gitHubUserUiState is GitHubUserUiState.Loading ->
                    CircularProgressIndicator()
                activeHeader == ActiveHeader.GitHubUser && fetchedUser != null -> AsyncImage(
                    model = fetchedUser.avatarUrl,
                    contentDescription = fetchedUser.login,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                else -> Text(text = "😎", fontSize = 36.sp)
            }
        }

        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        val errorMessage = when {
            activeHeader == ActiveHeader.Emoji && uiState is EmojiUiState.Error -> uiState.message
            activeHeader == ActiveHeader.GitHubUser && gitHubUserUiState is GitHubUserUiState.Error -> gitHubUserUiState.message
            else -> null
        }
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        MenuPanel(
            title = stringResource(R.string.menu_random_emoji),
            icon = Icons.Default.Refresh,
            onClick = {
                activeHeader = ActiveHeader.Emoji
                headerLocked = true
                onGetEmojiClick()
            }
        )

        MenuPanel(
            title = stringResource(R.string.menu_emoji_list),
            icon = Icons.Default.Menu,
            onClick = onEmojiListClick
        )

        GitHubUserSearch(
            onSearch = { username ->
                activeHeader = ActiveHeader.GitHubUser
                headerLocked = true
                onGitHubSearch(username)
            }
        )

        MenuPanel(
            title = stringResource(R.string.menu_avatar_list),
            icon = Icons.Default.Person,
            onClick = onAvatarListClick
        )

        MenuPanel(
            title = stringResource(R.string.menu_google_repos),
            icon = Icons.Default.Star,
            onClick = onGoogleReposClick
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun MenuPanel(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
