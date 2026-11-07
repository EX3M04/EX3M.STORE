package com.ex3m.store.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filed.Download
import androidx.compose.material.icons.filed.Install
import androidx.compose.material.icons.filed.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@Composable
fun DetailScreen(
    app: com.ex3m.store.data.model.AppCard,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle(DetailViewModel.DownloadState.Idle)

    val fullDescription = remember { mutableStateOf<String>("") }

    // Fetch full description
    androidx.compose.runtime.LaunchedEffect(app.appName) {
        // In a real app, this would fetch from the descriptions folder
        fullDescription.value = app.description
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Text(
                        text = app.appName,
                        fontWeight = FontWeight.Medium,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer
                )
            )

            // Content
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // App Icon
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(RoundedCornerShape(24.dp))
                            ) {
                                app.iconUrl?.let { url ->
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(url)
                                            .crossfade(true)
                                            .transformations(RoundedCornersTransformation(24.dp))
                                            .build(),
                                        contentDescription = "App icon for ${app.appName}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        placeholder = {
                                            Icons.Filled.Apps(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer),
                                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        },
                                        error = {
                                            Icons.Filled.BrokenImage(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(androidx.compose.material3.MaterialTheme.colorScheme.errorContainer),
                                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        }
                                    )
                                } ?: Icons.Filled.Apps(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer),
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // App Name & Version
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            arrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = app.appName,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Version ${app.version}",
                                fontSize = 16.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (app.apkSize > 0) {
                                Text(
                                    text = formatFileSize(app.apkSize),
                                    fontSize = 14.sp,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Description
                    item {
                        if (fullDescription.value.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    arrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Description",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = fullDescription.value,
                                        fontSize = 15.sp,
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                        }
                    }

                    // Download/Install Button
                    item {
                        DownloadActionButton(
                            downloadState = downloadState,
                            onDownloadClick = { viewModel.startDownload(app) },
                            onPauseClick = { downloadState.localPath?.let { viewModel.pauseDownload(0) } },
                            onInstallClick = { downloadState.localPath?.let { viewModel.installApk(it) } },
                            onCancelClick = { viewModel.cancelDownload(0) }
                        )
                    }

                    // Progress indicator with wave animation
                    if (downloadState.status == com.ex3m.store.data.model.DownloadStatus.DOWNLOADING ||
                        downloadState.status == com.ex3m.store.data.model.DownloadStatus.PAUSED) {
                        item {
                            WaveProgressIndicator(
                                progress = downloadState.progress,
                                status = downloadState.status,
                                downloaded = downloadState.downloadedBytes,
                                total = downloadState.totalBytes,
                                speed = ""
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadActionButton(
    downloadState: DetailViewModel.DownloadState,
    onDownloadClick: () -> Unit,
    onPauseClick: () -> Unit,
    onInstallClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val status = downloadState.status

    Button(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        onClick = when (status) {
            com.ex3m.store.data.model.DownloadStatus.PENDING,
            com.ex3m.store.data.model.DownloadStatus.FAILED -> onDownloadClick
            com.ex3m.store.data.model.DownloadStatus.DOWNLOADING -> onPauseClick
            com.ex3m.store.data.model.DownloadStatus.PAUSED -> onDownloadClick // Resume
            com.ex3m.store.data.model.DownloadStatus.COMPLETED -> onInstallClick
            com.ex3m.store.data.model.DownloadStatus.INSTALLING -> {}
            com.ex3m.store.data.model.DownloadStatus.INSTALLED -> {}
        },
        enabled = status != com.ex3m.store.data.model.DownloadStatus.INSTALLING,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = when (status) {
                com.ex3m.store.data.model.DownloadStatus.COMPLETED -> androidx.compose.material3.MaterialTheme.colorScheme.primary
                com.ex3m.store.data.model.DownloadStatus.INSTALLED -> androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHigh
                else -> androidx.compose.material3.MaterialTheme.colorScheme.primary
            },
            contentColor = when (status) {
                com.ex3m.store.data.model.DownloadStatus.INSTALLED -> androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                else -> androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
            }
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            arrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            when (status) {
                com.ex3m.store.data.model.DownloadStatus.PENDING,
                com.ex3m.store.data.model.DownloadStatus.FAILED -> {
                    Icon(Icons.Filed.Download, contentDescription = "Download", tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                    Text("Download", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
                com.ex3m.store.data.model.DownloadStatus.DOWNLOADING -> {
                    Icon(Icons.Filed.Pause, contentDescription = "Pause", tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                    Text("Pause", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
                com.ex3m.store.data.model.DownloadStatus.PAUSED -> {
                    Icon(Icons.Filed.Download, contentDescription = "Resume", tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                    Text("Resume", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
                com.ex3m.store.data.model.DownloadStatus.COMPLETED -> {
                    Icon(Icons.Filed.Install, contentDescription = "Install", tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
                    Text("Install", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
                com.ex3m.store.data.model.DownloadStatus.INSTALLING -> {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    )
                    Text("Installing...", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
                com.ex3m.store.data.model.DownloadStatus.INSTALLED -> {
                    Icon(Icons.Filed.Check, contentDescription = "Installed", tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
                    Text("Installed", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun WaveProgressIndicator(
    progress: Float,
    status: com.ex3m.store.data.model.DownloadStatus,
    downloaded: Long,
    total: Long,
    speed: String
) {
    val animatedProgress by androidx.compose.animation.animateFloatAsState(
        targetValue = progress,
        animationSpec = androidx.compose.animation.spring(
            dampingRatio = androidx.compose.animation.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.Spring.StiffnessMedium
        )
    )

    val waveOffset by androidx.compose.animation.animateFloatAsState(
        targetValue = 1f,
        animationSpec = androidx.compose.animation.infiniteRepeatable(
            animation = androidx.compose.animation.tween(1500, easing = androidx.compose.animation.LinearEasing),
            repeatMode = androidx.compose.animation.RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            arrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progress bar with wave animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                // Background
                androidx.compose.ui.graphics.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val width = size.width
                    val height = size.height
                    val waveHeight = height * 0.4f
                    val waveLength = width * 0.6f
                    val progressWidth = width * animatedProgress

                    // Draw wave background
                    val path = androidx.compose.ui.graphics.Path()
                    val waveCount = 3
                    val amplitude = waveHeight / 2

                    for (i in 0..waveCount) {
                        val phase = (waveOffset * 360 + i * 360f / waveCount) * Math.PI / 180
                        val x = (i * waveLength / waveCount - waveOffset * waveLength / waveCount) % width
                        path.moveTo(x, height)
                        path.cubicTo(
                            x + waveLength / waveCount / 4, height - amplitude * Math.sin(phase).toFloat(),
                            x + waveLength / waveCount * 3 / 4, height + amplitude * Math.sin(phase).toFloat(),
                            x + waveLength / waveCount, height
                        )
                    }
                    path.lineTo(width, height)
                    path.lineTo(0, height)
                    path.close()

                    drawPath(
                        path,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                    )
                }

                // Progress fill with wave
                Box(
                    modifier = Modifier
                        .width(progressWidth)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    androidx.compose.ui.graphics.Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val waveHeight = height * 0.4f
                        val waveLength = width * 0.6f
                        val amplitude = waveHeight / 2

                        val path = androidx.compose.ui.graphics.Path()
                        val waveCount = 3

                        for (i in 0..waveCount) {
                            val phase = (waveOffset * 360 + i * 360f / waveCount) * Math.PI / 180
                            val x = (i * waveLength / waveCount - waveOffset * waveLength / waveCount) % width
                            path.moveTo(x, height)
                            path.cubicTo(
                                x + waveLength / waveCount / 4, height - amplitude * Math.sin(phase).toFloat(),
                                x + waveLength / waveCount * 3 / 4, height + amplitude * Math.sin(phase).toFloat(),
                                x + waveLength / waveCount, height
                            )
                        }
                        path.lineTo(width, height)
                        path.lineTo(0, height)
                        path.close()

                        drawPath(
                            path,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Progress percentage text
                if (total > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            // Status info
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (status) {
                        com.ex3m.store.data.model.DownloadStatus.DOWNLOADING -> "Downloading..."
                        com.ex3m.store.data.model.DownloadStatus.PAUSED -> "Paused"
                        else -> "Preparing..."
                    },
                    fontSize = 13.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${formatFileSize(downloaded)} / ${formatFileSize(total)}",
                    fontSize = 13.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (speed.isNotBlank()) {
                Text(
                    text = "Speed: $speed",
                    fontSize = 12.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun LocalContext.current: android.content.Context = this as android.content.Context

fun formatFileSize(bytes: Long): String {
    return when {
        bytes >= 1_000_000_000 -> String.format("%.1f GB", bytes / 1_000_000_000.0)
        bytes >= 1_000_000 -> String.format("%.1f MB", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format("%.1f KB", bytes / 1_000.0)
        else -> "$bytes B"
    }
}