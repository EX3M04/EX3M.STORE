package com.ex3m.store.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val activeDownloadsCount by viewModel.activeDownloadsCount.collectAsStateWithLifecycle(0)

    Column(modifier = Modifier.fillMaxSize()) {
        // Main content area
        androidx.compose.foundation.layout.Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f)
        ) {
            when (selectedTab) {
                MainTab.STORE -> StoreScreen(viewModel = viewModel)
                MainTab.DOWNLOADS -> DownloadsScreen()
                MainTab.SETTINGS -> SettingsScreen()
            }
        }

        // Bottom navigation bar
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
        ) {
            val tabs = listOf(
                MainTab.STORE to Icons.Filled.Storefront,
                MainTab.DOWNLOADS to Icons.Filled.Download,
                MainTab.SETTINGS to Icons.Filled.Settings
            )

            tabs.forEachIndexed { index, (tab, icon) ->
                val isSelected = selectedTab == tab
                val badgeCount = if (tab == MainTab.DOWNLOADS) activeDownloadsCount else 0

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.name,
                            tint = if (isSelected)
                                androidx.compose.material3.MaterialTheme.colorScheme.primary
                            else
                                androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = {
                        Text(
                            text = tab.name,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            color = if (isSelected)
                                androidx.compose.material3.MaterialTheme.colorScheme.primary
                            else
                                androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    selected = isSelected,
                    onClick = { viewModel.onTabSelected(tab) },
                    badge = if (badgeCount > 0) {
                        {
                            androidx.compose.material3.Badge(
                                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.error,
                                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onError,
                                badgeContent = {
                                    Text(
                                        text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                                        fontSize = 10.sp,
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onError
                                    )
                                }
                            ) {}
                        }
                    } else null,
                    alwaysShowLabel = true
                )
            }
        }
    }
}

@Composable
fun StoreScreen(viewModel: MainViewModel = hiltViewModel()) {
    val apps by viewModel.apps.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)
    val scrollState = rememberScrollState()

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = scrollState
        ) {
            if (isLoading && apps.isEmpty()) {
                item {
                    LoadingIndicator()
                }
            }

            if (error != null) {
                item {
                    ErrorView(message = error!!, onRetry = { viewModel.retryLoad() })
                }
            }

            if (apps.isEmpty() && !isLoading) {
                item {
                    EmptyStateView()
                }
            }

            items(apps) { app ->
                AppCardView(app = app, onClick = {
                    // Navigate to detail screen
                    // TODO: Implement navigation
                })
            }
        }

        if (isLoading && apps.isNotEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(androidx.compose.ui.Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = androidx.compose.ui.Modifier
            .size(48.dp)
            .align(androidx.compose.ui.Alignment.CenterHorizontally),
        color = androidx.compose.material3.MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        arrangement = Arrangement.spacedBy(16.dp)
    ) {
        androidx.compose.material.icons.filled.Error(
            modifier = androidx.compose.ui.Modifier.size(64.dp),
            tint = androidx.compose.material3.MaterialTheme.colorScheme.error
        )
        androidx.compose.material3.Text(
            text = message,
            textAlign = androidx.compose.ui.text.TextAlign.Center,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
        androidx.compose.material3.Button(onClick = onRetry) {
            androidx.compose.material3.Text("Retry")
        }
    }
}

@Composable
fun EmptyStateView() {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        arrangement = Arrangement.spacedBy(16.dp)
    ) {
        androidx.compose.material.icons.filled.Inbox(
            modifier = androidx.compose.ui.Modifier.size(64.dp),
            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        androidx.compose.material3.Text(
            text = "No apps available",
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        androidx.compose.material3.Text(
            text = "Check back later for new releases",
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}