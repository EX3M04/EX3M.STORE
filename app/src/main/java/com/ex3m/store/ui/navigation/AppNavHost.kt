package com.ex3m.store.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ex3m.store.data.model.AppCard
import com.ex3m.store.ui.detail.DetailScreen
import com.ex3m.store.ui.detail.DetailViewModel
import com.ex3m.store.ui.downloads.DownloadsScreen
import com.ex3m.store.ui.downloads.DownloadsViewModel
import com.ex3m.store.ui.main.MainScreen
import com.ex3m.store.ui.main.MainViewModel
import com.ex3m.store.ui.settings.SettingsScreen
import com.ex3m.store.ui.settings.SettingsViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") {
            MainScreen(onNavigateToDetail = { app ->
                navController.navigate("detail/${app.appName}")
            })
        }

        composable(
            route = "detail/{appName}",
            arguments = listOf(androidx.navigation.navArgument("appName") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val appName = backStackEntry.getString() ?: ""
            // In a real app, you'd pass the full AppCard object or fetch it
            DetailScreen(
                app = createDummyAppCard(appName),
                onBack = { navController.popBackStack() }
            )
        }

        composable("downloads") {
            DownloadsScreen()
        }

        composable("settings") {
            SettingsScreen()
        }
    }
}

// Dummy function - in real app you'd get the actual AppCard from repository
private fun createDummyAppCard(appName: String): AppCard {
    return AppCard(
        release = com.ex3m.store.data.model.GitHubRelease(
            id = 0,
            tag_name = "1.0.0",
            name = appName,
            body = "Description for $appName",
            draft = false,
            prerelease = false,
            created_at = "",
            published_at = "",
            assets = emptyList()
        ),
        iconUrl = null,
        description = "Description for $appName"
    )
}

// Update MainScreen to accept navigation callback
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToDetail: (AppCard) -> Unit
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val activeDownloadsCount by viewModel.activeDownloadsCount.collectAsStateWithLifecycle(0)

    Column(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.layout.Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f)
        ) {
            when (selectedTab) {
                MainTab.STORE -> StoreScreen(viewModel = viewModel, onAppClick = onNavigateToDetail)
                MainTab.DOWNLOADS -> DownloadsScreen()
                MainTab.SETTINGS -> SettingsScreen()
            }
        }

        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
        ) {
            val tabs = listOf(
                MainTab.STORE to androidx.compose.material.icons.Icons.Filled.Storefront,
                MainTab.DOWNLOADS to androidx.compose.material.icons.Icons.Filled.Download,
                MainTab.SETTINGS to androidx.compose.material.icons.Icons.Filled.Settings
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
fun StoreScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onAppClick: (AppCard) -> Unit
) {
    val apps by viewModel.apps.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle(null)
    val scrollState = androidx.compose.foundation.rememberScrollState()

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
                AppCardView(app = app, onClick = { onAppClick(app) })
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