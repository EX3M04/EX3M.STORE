package com.ex3m.store.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filed.ArrowBack
import androidx.compose.material.icons.filed.Brush
import androidx.compose.material.icons.filed.CloudDownload
import androidx.compose.material.icons.filed.Info
import androidx.compose.material.icons.filed.Storage
import androidx.compose.material.icons.filed.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemHeadline
import androidx.compose.material3.ListItemLeadingIcon
import androidx.compose.material3.ListItemMetadata
import androidx.compose.material3.ListItemSecondaryText
import androidx.compose.material3.ListItemTrailingIcon
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle(0)
    val useDynamicColors by viewModel.useDynamicColors.collectAsStateWithLifecycle(true)
    val cardElevation by viewModel.cardElevation.collectAsStateWithLifecycle(8f)
    val cardCornerRadius by viewModel.cardCornerRadius.collectAsStateWithLifecycle(28f)
    val cardScaleOnPress by viewModel.cardScaleOnPress.collectAsStateWithLifecycle(true)
    val animationSpeed by viewModel.animationSpeed.collectAsStateWithLifecycle(1f)
    val showAppVersionOnCard by viewModel.showAppVersionOnCard.collectAsStateWithLifecycle(true)
    val showAppSizeOnCard by viewModel.showAppSizeOnCard.collectAsStateWithLifecycle(true)
    val compactCardMode by viewModel.compactCardMode.collectAsStateWithLifecycle(false)
    val downloadOverWifiOnly by viewModel.downloadOverWifiOnly.collectAsStateWithLifecycle(false)
    val autoInstallAfterDownload by viewModel.autoInstallAfterDownload.collectAsStateWithLifecycle(false)
    val showDownloadNotification by viewModel.showDownloadNotification.collectAsStateWithLifecycle(true)
    val maxConcurrentDownloads by viewModel.maxConcurrentDownloads.collectAsStateWithLifecycle(3)
    val showPrereleaseApps by viewModel.showPrereleaseApps.collectAsStateWithLifecycle(false)
    val autoRefreshInterval by viewModel.autoRefreshInterval.collectAsStateWithLifecycle(30)
    val enableAnalytics by viewModel.enableAnalytics.collectAsStateWithLifecycle(false)
    val enableDebugLogging by viewModel.enableDebugLogging.collectAsStateWithLifecycle(false)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { /* Navigation handled by parent */ }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Medium,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
                )
            )

            // Settings content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Appearance Section
                item {
                    SettingsSection(title = "Appearance", icon = Icons.Filled.Brush) {
                        SettingsCard {
                            // Theme Mode
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Theme Mode")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Choose app theme")
                                },
                                trailingContent = {
                                    ThemeModeSelector(
                                        selectedMode = themeMode,
                                        onModeChange = { viewModel.setThemeMode(it) }
                                    )
                                }
                            )

                            Divider()

                            // Dynamic Colors
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Dynamic Colors (Material You)")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Use wallpaper-based colors on Android 12+")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = useDynamicColors,
                                        onCheckedChange = { viewModel.setUseDynamicColors(it) }
                                    )
                                }
                            )

                            Divider()

                            // Card Elevation
                            SettingSlider(
                                label = "Card Elevation",
                                description = "Shadow depth of app cards",
                                value = cardElevation,
                                valueRange = 0f..24f,
                                onValueChange = { viewModel.setCardElevation(it) },
                                formatValue = { "${it.toInt()}dp" }
                            )

                            Divider()

                            // Card Corner Radius
                            SettingSlider(
                                label = "Card Corner Radius",
                                description = "Roundness of card corners",
                                value = cardCornerRadius,
                                valueRange = 0f..40f,
                                onValueChange = { viewModel.setCardCornerRadius(it) },
                                formatValue = { "${it.toInt()}dp" }
                            )

                            Divider()

                            // Card Scale on Press
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Card Press Animation")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Scale card when pressed")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = cardScaleOnPress,
                                        onCheckedChange = { viewModel.setCardScaleOnPress(it) }
                                    )
                                }
                            )

                            Divider()

                            // Animation Speed
                            SettingSlider(
                                label = "Animation Speed",
                                description = "Global animation speed multiplier",
                                value = animationSpeed,
                                valueRange = 0.5f..2f,
                                onValueChange = { viewModel.setAnimationSpeed(it) },
                                formatValue = { String.format("%.1fx", it) }
                            )

                            Divider()

                            // Show Version on Card
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Show Version on Card")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Display app version in store cards")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = showAppVersionOnCard,
                                        onCheckedChange = { viewModel.setShowAppVersionOnCard(it) }
                                    )
                                }
                            )

                            Divider()

                            // Show Size on Card
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Show Size on Card")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Display APK size in store cards")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = showAppSizeOnCard,
                                        onCheckedChange = { viewModel.setShowAppSizeOnCard(it) }
                                    )
                                }
                            )

                            Divider()

                            // Compact Card Mode
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Compact Card Mode")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Smaller cards with less spacing")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = compactCardMode,
                                        onCheckedChange = { viewModel.setCompactCardMode(it) }
                                    )
                                }
                            )
                        }
                    }
                }

                // Downloads Section
                item {
                    SettingsSection(title = "Downloads", icon = Icons.Filled.CloudDownload) {
                        SettingsCard {
                            // Download over WiFi only
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Wi-Fi Only Downloads")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Only download on Wi-Fi networks")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = downloadOverWifiOnly,
                                        onCheckedChange = { viewModel.setDownloadOverWifiOnly(it) }
                                    )
                                }
                            )

                            Divider()

                            // Auto Install
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Auto Install After Download")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Automatically open install prompt when download completes")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = autoInstallAfterDownload,
                                        onCheckedChange = { viewModel.setAutoInstallAfterDownload(it) }
                                    )
                                }
                            )

                            Divider()

                            // Show Download Notification
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Download Notifications")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Show progress in notification shade")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = showDownloadNotification,
                                        onCheckedChange = { viewModel.setShowDownloadNotification(it) }
                                    )
                                }
                            )

                            Divider()

                            // Max Concurrent Downloads
                            SettingSlider(
                                label = "Max Concurrent Downloads",
                                description = "Number of simultaneous downloads",
                                value = maxConcurrentDownloads.toFloat(),
                                valueRange = 1f..5f,
                                onValueChange = { viewModel.setMaxConcurrentDownloads(it.toInt()) },
                                formatValue = { "${it.toInt()}" }
                            )
                        }
                    }
                }

                // Store Section
                item {
                    SettingsSection(title = "Store", icon = Icons.Filled.Storage) {
                        SettingsCard {
                            // Show Prerelease Apps
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Show Pre-release Apps")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Display alpha/beta releases in store")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = showPrereleaseApps,
                                        onCheckedChange = { viewModel.setShowPrereleaseApps(it) }
                                    )
                                }
                            )

                            Divider()

                            // Auto Refresh Interval
                            SettingSlider(
                                label = "Auto Refresh Interval",
                                description = "Minutes between automatic store refresh",
                                value = autoRefreshInterval.toFloat(),
                                valueRange = 0f..120f,
                                onValueChange = { viewModel.setAutoRefreshInterval(it.toInt()) },
                                formatValue = { if (it == 0f) "Off" else "${it.toInt()} min" }
                            )

                            Divider()

                            // Enable Analytics
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Enable Analytics")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Help improve the app with anonymous usage data")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = enableAnalytics,
                                        onCheckedChange = { viewModel.setEnableAnalytics(it) }
                                    )
                                }
                            )
                        }
                    }
                }

                // Advanced Section
                item {
                    SettingsSection(title = "Advanced", icon = Icons.Filled.Tune) {
                        SettingsCard {
                            // Debug Logging
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Debug Logging")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Enable verbose logging for troubleshooting")
                                },
                                trailingContent = {
                                    Checkbox(
                                        checked = enableDebugLogging,
                                        onCheckedChange = { viewModel.setEnableDebugLogging(it) }
                                    )
                                }
                            )

                            Divider()

                            // GitHub Token
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "GitHub Token")
                                },
                                supportingContent = {
                                    ListItemSecondaryText(text = "Personal access token for higher rate limits")
                                },
                                trailingContent = {
                                    Text(text = "Set", color = androidx.compose.material3.MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium, modifier = Modifier.padding(end = 16.dp))
                                }
                            )
                        }
                    }
                }

                // About Section
                item {
                    SettingsSection(title = "About", icon = Icons.Filled.Info) {
                        SettingsCard {
                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Version")
                                },
                                trailingContent = {
                                    Text(text = "1.0.0", color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            )

                            Divider()

                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "Source Code")
                                },
                                trailingContent = {
                                    Text(text = "GitHub", color = androidx.compose.material3.MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                }
                            )

                            Divider()

                            ListItem(
                                modifier = Modifier.fillMaxWidth(),
                                headlineContent = {
                                    ListItemHeadline(text = "License")
                                },
                                trailingContent = {
                                    Text(text = "MIT", color = androidx.compose.material3.MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        arrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section header
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            arrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
        }

        content()
    }
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            arrangement = Arrangement.spacedBy(0.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ThemeModeSelector(
    selectedMode: Int,
    onModeChange: (Int) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf(
            0 to "System",
            1 to "Light",
            2 to "Dark"
        ).forEach { (mode, label) ->
            val isSelected = selectedMode == mode
            androidx.compose.material3.Button(
                onClick = { onModeChange(mode) },
                modifier = Modifier.weight(1f, fill = false).padding(horizontal = 4.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (isSelected)
                        androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                    else
                        androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
                )
            ) {
                Text(
                    text = label,
                    color = if (isSelected)
                        androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SettingSlider(
    label: String,
    description: String,
    value: Float,
    valueRange: androidx.compose.ui.unit.ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    formatValue: (Float) -> String
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        arrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = label, fontSize = 16.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
                Text(text = description, fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(text = formatValue(value), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
        }

        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = ((valueRange.endInclusive - valueRange.start) / 1f).toInt(),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                trackColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                activeTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                inactiveTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHighest
            )
        )
    }
}