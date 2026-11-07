package com.ex3m.store.ui.downloads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filed.Delete
import androidx.compose.material.icons.filed.Download
import androidx.compose.material.icons.filed.Install
import androidx.compose.material.icons.filed.Pause
import androidx.compose.material.icons.filed.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@Composable
fun DownloadsScreen(viewModel: DownloadsViewModel = hiltViewModel()) {
    val downloads by viewModel.downloads.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Downloads",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
            }

            // Downloads list
            if (isLoading && downloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } else if (downloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        arrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icons.Filed.Download(
                            modifier = Modifier.size(64.dp),
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "No downloads yet",
                            fontSize = 18.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Download apps from the store to see them here",
                            fontSize = 14.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(downloads) { download ->
                        DownloadItemView(
                            download = download,
                            onActionClick = { viewModel.onActionClick(download) },
                            onDeleteClick = { viewModel.deleteDownload(download.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItemView(
    download: com.ex3m.store.data.model.DownloadEntity,
    onActionClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val status = download.status
    val progress = download.progress

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            arrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with name, version, and delete button
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    arrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = download.appName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "v${download.version}",
                        fontSize = 13.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filed.Delete,
                        contentDescription = "Delete",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Progress bar
            if (status == com.ex3m.store.data.model.DownloadStatus.DOWNLOADING ||
                status == com.ex3m.store.data.model.DownloadStatus.PAUSED ||
                status == com.ex3m.store.data.model.DownloadStatus.PENDING) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    arrangement = Arrangement.spacedBy(4.dp)
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (status) {
                                com.ex3m.store.data.model.DownloadStatus.DOWNLOADING -> "Downloading..."
                                com.ex3m.store.data.model.DownloadStatus.PAUSED -> "Paused"
                                com.ex3m.store.data.model.DownloadStatus.PENDING -> "Pending..."
                                else -> ""
                            },
                            fontSize = 12.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                    }

                    androidx.compose.material3.LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = progress,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        trackColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                    )

                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${formatFileSize(download.downloadedBytes)} / ${formatFileSize(download.totalBytes)}",
                            fontSize = 11.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Action button
            Button(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                onClick = onActionClick,
                enabled = status != com.ex3m.store.data.model.DownloadStatus.INSTALLING &&
                        status != com.ex3m.store.data.model.DownloadStatus.INSTALLED,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = when (status) {
                        com.ex3m.store.data.model.DownloadStatus.COMPLETED -> androidx.compose.material3.MaterialTheme.colorScheme.primary
                        com.ex3m.store.data.model.DownloadStatus.FAILED -> androidx.compose.material3.MaterialTheme.colorScheme.errorContainer
                        else -> androidx.compose.material3.MaterialTheme.colorScheme.primary
                    },
                    contentColor = when (status) {
                        com.ex3m.store.data.model.DownloadStatus.FAILED -> androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
                        else -> androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    }
                )
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    arrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (status) {
                        com.ex3m.store.data.model.DownloadStatus.PENDING,
                        com.ex3m.store.data.model.DownloadStatus.FAILED -> {
                            Icon(Icons.Filed.Download, contentDescription = "Download", tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                            Text("Download", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                        com.ex3m.store.data.model.DownloadStatus.DOWNLOADING -> {
                            Icon(Icons.Filed.Pause, contentDescription = "Pause", tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                            Text("Pause", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                        com.ex3m.store.data.model.DownloadStatus.PAUSED -> {
                            Icon(Icons.Filed.PlayArrow, contentDescription = "Resume", tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                            Text("Resume", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                        com.ex3m.store.data.model.DownloadStatus.COMPLETED -> {
                            Icon(Icons.Filed.Install, contentDescription = "Install", tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                            Text("Install", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                        com.ex3m.store.data.model.DownloadStatus.INSTALLING -> {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                            )
                            Text("Installing...", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                        com.ex3m.store.data.model.DownloadStatus.INSTALLED -> {
                            Icon(Icons.Filed.Check, contentDescription = "Installed", tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
                            Text("Installed", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes >= 1_000_000_000 -> String.format("%.1f GB", bytes / 1_000_000_000.0)
        bytes >= 1_000_000 -> String.format("%.1f MB", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format("%.1f KB", bytes / 1_000.0)
        else -> "$bytes B"
    }
}