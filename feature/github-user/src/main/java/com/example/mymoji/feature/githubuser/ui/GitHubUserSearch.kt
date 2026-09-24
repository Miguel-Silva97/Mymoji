package com.example.mymoji.feature.githubuser.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.mymoji.feature.githubuser.R

@Composable
fun GitHubUserSearch(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var username by rememberSaveable { mutableStateOf("") }

    OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        modifier = modifier.fillMaxWidth(),
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
}
