package com.example.mymoji.feature.googlerepos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mymoji.feature.googlerepos.R
import com.example.mymoji.feature.googlerepos.domain.model.GitHubRepo
import com.example.mymoji.feature.googlerepos.presentation.GoogleReposUiState
import com.example.mymoji.feature.googlerepos.presentation.GoogleReposViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleReposScreen(
    uiState: GoogleReposUiState,
    onPageSizeChange: (Int) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onRetry: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.repos_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PageSizeSelector(selected = uiState.pageSize, onSelect = onPageSizeChange)
            HorizontalDivider()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                ReposContent(uiState = uiState, onRetry = onRetry)
            }
            HorizontalDivider()
            PageNavigation(uiState = uiState, onPreviousPage = onPreviousPage, onNextPage = onNextPage)
        }
    }
}

@Composable
private fun PageSizeSelector(selected: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.repos_page_size),
            style = MaterialTheme.typography.labelLarge
        )
        GoogleReposViewModel.PAGE_SIZE_OPTIONS.forEach { size ->
            FilterChip(
                selected = size == selected,
                onClick = { onSelect(size) },
                label = { Text(size.toString()) }
            )
        }
    }
}

@Composable
private fun ReposContent(uiState: GoogleReposUiState, onRetry: () -> Unit) {
    when {
        uiState.isLoading -> CenteredBox { CircularProgressIndicator() }
        uiState.errorMessageRes != null -> CenteredBox {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(uiState.errorMessageRes),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedButton(onClick = onRetry) {
                    Text(stringResource(R.string.repos_retry))
                }
            }
        }
        uiState.repos.isEmpty() -> CenteredBox {
            Text(
                text = stringResource(R.string.repos_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(uiState.repos, key = { it.name }) { repo ->
                RepoRow(repo)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun RepoRow(repo: GitHubRepo) {
    ListItem(
        headlineContent = { Text(repo.name) },
        supportingContent = repo.description?.let { description ->
            { Text(description, maxLines = 2, overflow = TextOverflow.Ellipsis) }
        }
    )
}

@Composable
private fun PageNavigation(uiState: GoogleReposUiState, onPreviousPage: () -> Unit, onNextPage: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onPreviousPage,
            enabled = uiState.hasPreviousPage && !uiState.isLoading
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
            Text(stringResource(R.string.repos_previous))
        }
        Text(
            text = stringResource(R.string.repos_page, uiState.page),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        TextButton(
            onClick = onNextPage,
            enabled = uiState.hasNextPage && !uiState.isLoading
        ) {
            Text(stringResource(R.string.repos_next))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
