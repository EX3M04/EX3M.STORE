package com.ex3m.store.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GitHubRelease(
    val id: Long,
    val tag_name: String,
    val name: String,
    val body: String?,
    val draft: Boolean,
    val prerelease: Boolean,
    val created_at: String,
    val published_at: String,
    val assets: List<GitHubAsset>
) {
    val appName: String
        get() = name

    val version: String
        get() = tag_name

    val description: String
        get() = body ?: ""

    val iconAsset: GitHubAsset?
        get() = assets.find { it.name.lowercase().endsWith(".jpg") || it.name.lowercase().endsWith(".png") }

    val apkAsset: GitHubAsset?
        get() = assets.find { it.name.lowercase().endsWith(".apk") }
}

@Serializable
data class GitHubAsset(
    val id: Long,
    val name: String,
    val browser_download_url: String,
    val size: Long,
    val content_type: String
)

@Serializable
data class AppCard(
    val release: GitHubRelease,
    val iconUrl: String?,
    val description: String
) {
    val appName: String = release.appName
    val version: String = release.version
    val downloadUrl: String? = release.apkAsset?.browser_download_url
    val apkSize: Long = release.apkAsset?.size ?: 0
}

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appName: String,
    val version: String,
    val downloadUrl: String,
    val localPath: String?,
    val status: DownloadStatus,
    val progress: Float,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    INSTALLING,
    INSTALLED
}

@Serializable
data class DownloadProgress(
    val downloadId: Long,
    val progress: Float,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val status: DownloadStatus,
    val speed: String = ""
)