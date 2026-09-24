package com.example.mymoji.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mymoji.R
import com.example.mymoji.feature.emoji.presentation.EmojiUiState
import com.example.mymoji.feature.githubuser.presentation.GitHubUserUiState
import com.example.mymoji.feature.githubuser.ui.GitHubUserSearch
import com.example.mymoji.presentation.HeaderItem

private enum class ActiveHeader { Emoji, GitHubUser }

@Composable
fun HomeScreen(
    headerItem: HeaderItem? = null,
    onHeaderClick: () -> Unit = {},
    uiState: EmojiUiState = EmojiUiState.Idle,
    gitHubUserUiState: GitHubUserUiState = GitHubUserUiState.Idle,
    onGetEmojiClick: () -> Unit = {},
    onEmojiListClick: () -> Unit = {},
    onGitHubSearch: (String) -> Unit = {},
    onAvatarListClick: () -> Unit = {},
    onGoogleReposClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var activeHeader by rememberSaveable { mutableStateOf<ActiveHeader?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        val isActiveLoading = when (activeHeader) {
            ActiveHeader.Emoji -> uiState is EmojiUiState.Loading
            ActiveHeader.GitHubUser -> gitHubUserUiState is GitHubUserUiState.Loading
            null -> false
        }

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(MaterialTheme.shapes.large)
                .clickable(
                    enabled = headerItem != null && !isActiveLoading,
                    role = Role.Button,
                    onClick = onHeaderClick
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isActiveLoading -> CircularProgressIndicator()
                headerItem is HeaderItem.EmojiItem -> AsyncImage(
                    model = headerItem.emoji.url,
                    contentDescription = headerItem.emoji.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                headerItem is HeaderItem.AvatarItem -> AsyncImage(
                    model = headerItem.user.avatarUrl,
                    contentDescription = headerItem.user.login,
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
            activeHeader == ActiveHeader.GitHubUser && gitHubUserUiState is GitHubUserUiState.Error ->
                stringResource(gitHubUserUiState.messageRes)
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
