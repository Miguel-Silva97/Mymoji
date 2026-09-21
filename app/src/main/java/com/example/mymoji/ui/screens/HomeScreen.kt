package com.example.mymoji.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mymoji.R
import com.example.mymoji.presentation.viewmodel.EmojiUiState
import com.example.mymoji.ui.theme.MymojiTheme

@Composable
fun HomeScreen(
    uiState: EmojiUiState = EmojiUiState.Idle,
    onGetEmojiClick: () -> Unit = {},
    onEmojiListClick: () -> Unit = {},
    onGitHubSearch: (String) -> Unit = {},
    onAvatarListClick: () -> Unit = {},
    onGoogleReposClick: () -> Unit = {}
) {
    var githubUsername by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Emoji header: shows the default emoji until a random one has been fetched,
        // then that fetched emoji replaces it in place.
        val fetchedEmoji = (uiState as? EmojiUiState.Success)?.currentRandomEmoji
        Box(
            modifier = Modifier
                .size(72.dp)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState is EmojiUiState.Loading -> CircularProgressIndicator()
                fetchedEmoji != null -> AsyncImage(
                    model = fetchedEmoji.url,
                    contentDescription = fetchedEmoji.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                else -> Text(text = "😎", fontSize = 72.sp)
            }
        }

        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState is EmojiUiState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // First 2 menu items
        MenuPanel(
            title = stringResource(R.string.menu_random_emoji),
            icon = Icons.Default.Refresh,
            onClick = onGetEmojiClick
        )

        MenuPanel(
            title = stringResource(R.string.menu_emoji_list),
            icon = Icons.Default.Menu,
            onClick = onEmojiListClick
        )

        // GitHub Search Field
        GitHubSearchField(
            value = githubUsername,
            onValueChange = { githubUsername = it },
            onSearch = { onGitHubSearch(githubUsername) }
        )

        // Bottom 2 menu items
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
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun GitHubSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.search_hint)) },
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MymojiTheme {
        HomeScreen()
    }
}
