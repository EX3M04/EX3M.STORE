package com.ex3m.store.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filed.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation

@Composable
fun AppCardView(
    app: com.ex3m.store.data.model.AppCard,
    onClick: () -> Unit
) {
    val cardElevation = 8.dp
    val cardCornerRadius = 28.dp
    val cardShape = RoundedCornerShape(cardCornerRadius)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .clickable(onClick = onClick)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {}
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerLow,
            pressedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation,
            pressedElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    app.iconUrl?.let { url ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .crossfade(true)
                                .transformations(RoundedCornersTransformation(16.dp))
                                .build(),
                            contentDescription = "App icon for ${app.appName}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = {
                                androidx.compose.material.icons.filled.Apps(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer),
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            },
                            error = {
                                androidx.compose.material.icons.filled.BrokenImage(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(androidx.compose.material3.MaterialTheme.colorScheme.errorContainer),
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        )
                    } ?: androidx.compose.material.icons.filled.Apps(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer),
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // App Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = app.appName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "v${app.version}",
                        fontSize = 14.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (app.description.isNotBlank()) {
                        Text(
                            text = app.description,
                            fontSize = 13.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // File size
                    if (app.apkSize > 0) {
                        Text(
                            text = formatFileSize(app.apkSize),
                            fontSize = 12.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Arrow indicator
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Open app details",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
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