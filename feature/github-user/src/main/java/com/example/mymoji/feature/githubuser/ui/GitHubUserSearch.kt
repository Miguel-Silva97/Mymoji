package com.example.mymoji.feature.githubuser.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.mymoji.feature.githubuser.R
import com.example.mymoji.feature.githubuser.presentation.GitHubUserUiState

@Composable
fun GitHubUserSearch(
    uiState: GitHubUserUiState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.search_hint)) },
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            trailingIcon = {
                IconButton(onClick = { onSearch(username) }) {
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

        GitHubUserResult(uiState = uiState)
    }
}

@Composable
private fun GitHubUserResult(uiState: GitHubUserUiState) {
    when (uiState) {
        GitHubUserUiState.Idle -> Unit

        GitHubUserUiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(8.dp))

        is GitHubUserUiState.Success -> ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = uiState.user.avatarUrl,
                    contentDescription = uiState.user.login,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = uiState.user.login,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        is GitHubUserUiState.Error -> Text(
            text = uiState.message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}
