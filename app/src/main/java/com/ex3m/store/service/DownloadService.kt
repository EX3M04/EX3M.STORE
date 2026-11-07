package com.ex3m.store.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleService
import com.ex3m.store.data.db.DownloadDao
import com.ex3m.store.data.model.DownloadEntity
import com.ex3m.store.data.model.DownloadStatus
import com.ex3m.store.data.prefs.SettingsPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class DownloadService : LifecycleService() {

    companion object {
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID_BASE = 1000
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_CANCEL = "action_cancel"
        const val ACTION_INSTALL = "action_install"
    }

    @Inject
    lateinit var downloadDao: DownloadDao

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    @Inject
    lateinit var okHttpClient: OkHttpClient

    private val downloadJobs = mutableMapOf<Long, Job>()
    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Download progress notifications"
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun startDownload(app: com.ex3m.store.data.model.AppCard) {
        val downloadUrl = app.downloadUrl ?: return
        val fileName = "${app.appName.replace(" ", "_")}_v${app.version}.apk"
        val downloadDir = getExternalFilesDir(null) ?: filesDir
        val destinationFile = File(downloadDir, fileName)

        val entity = DownloadEntity(
            appName = app.appName,
            version = app.version,
            downloadUrl = downloadUrl,
            localPath = destinationFile.absolutePath,
            status = DownloadStatus.PENDING,
            progress = 0f,
            totalBytes = app.apkSize,
            downloadedBytes = 0
        )

        CoroutineScope(Dispatchers.IO).launch {
            val downloadId = downloadDao.insert(entity)
            downloadFile(downloadId, downloadUrl, destinationFile, app.apkSize)
        }
    }

    private fun downloadFile(downloadId: Long, url: String, destination: File, totalSize: Long) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                updateStatus(downloadId, DownloadStatus.DOWNLOADING)

                val request = Request.Builder().url(url).build()
                val response = okHttpClient.newCall(request).execute()

                if (!response.isSuccessful) {
                    throw Exception("HTTP ${response.code()}")
                }

                val responseBody = response.body ?: throw Exception("Empty response body")
                val contentLength = responseBody.contentLength()
                val totalBytes = if (contentLength > 0) contentLength else totalSize

                updateTotalBytes(downloadId, totalBytes)

                destination.parentFile?.mkdirs()
                val fileOutputStream = FileOutputStream(destination)

                val buffer = ByteArray(8192)
                var downloaded = 0L
                var lastUpdateTime = System.currentTimeMillis()
                var lastDownloaded = 0L

                responseBody.byteStream().use { input ->
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break

                        fileOutputStream.write(buffer, 0, read)
                        downloaded += read

                        val now = System.currentTimeMillis()
                        if (now - lastUpdateTime >= 500) { // Update every 500ms
                            val speed = (downloaded - lastDownloaded) * 1000 / (now - lastUpdateTime)
                            updateProgress(downloadId, downloaded, totalBytes, formatSpeed(speed))
                            lastUpdateTime = now
                            lastDownloaded = downloaded
                        }

                        // Check if cancelled
                        if (downloadJobs[downloadId]?.isCancelled == true) {
                            throw InterruptedException("Download cancelled")
                        }
                    }
                }

                fileOutputStream.close()
                responseBody.close()

                updateStatus(downloadId, DownloadStatus.COMPLETED, progress = 1f, downloadedBytes = totalBytes)
                showCompletionNotification(downloadId)

            } catch (e: Exception) {
                if (e !is InterruptedException) {
                    updateStatus(downloadId, DownloadStatus.FAILED, error = e.message)
                    showErrorNotification(downloadId, e.message ?: "Download failed")
                }
                destination.delete()
            } finally {
                downloadJobs.remove(downloadId)
            }
        }

        downloadJobs[downloadId] = job
    }

    private fun updateStatus(
        downloadId: Long,
        status: DownloadStatus,
        progress: Float = -1f,
        downloadedBytes: Long = -1,
        totalBytes: Long = -1,
        error: String? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = downloadDao.getById(downloadId).first() ?: return@launch

            val updatedEntity = entity.copy(
                status = status,
                progress = if (progress >= 0) progress else entity.progress,
                downloadedBytes = if (downloadedBytes >= 0) downloadedBytes else entity.downloadedBytes,
                totalBytes = if (totalBytes >= 0) totalBytes else entity.totalBytes,
                updatedAt = System.currentTimeMillis()
            )

            downloadDao.update(updatedEntity)

            if (status == DownloadStatus.DOWNLOADING || status == DownloadStatus.PAUSED) {
                showProgressNotification(downloadId, updatedEntity)
            }
        }
    }

    private fun updateProgress(downloadId: Long, downloaded: Long, total: Long, speed: String) {
        val progress = if (total > 0) downloaded.toFloat() / total else 0f
        updateStatus(downloadId, DownloadStatus.DOWNLOADING, progress = progress, downloadedBytes = downloaded)
    }

    private fun updateTotalBytes(downloadId: Long, totalBytes: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = downloadDao.getById(downloadId).first() ?: return@launch
            downloadDao.update(entity.copy(totalBytes = totalBytes, updatedAt = System.currentTimeMillis()))
        }
    }

    private fun showProgressNotification(downloadId: Long, entity: DownloadEntity) {
        val progress = (entity.progress * 100).toInt()
        val downloadedStr = formatFileSize(entity.downloadedBytes)
        val totalStr = formatFileSize(entity.totalBytes)

        val pauseIntent = Intent(this, DownloadService::class.java).apply {
            action = ACTION_PAUSE
            putExtra("download_id", downloadId)
        }
        val cancelIntent = Intent(this, DownloadService::class.java).apply {
            action = ACTION_CANCEL
            putExtra("download_id", downloadId)
        }

        val pauseAction = NotificationCompat.Action(
            androidx.compose.material.icons.filled.Pause.toString().toInt(),
            "Pause",
            PendingIntent.getService(this, downloadId.toInt(), pauseIntent, PendingIntent.FLAG_IMMUTABLE)
        )
        val cancelAction = NotificationCompat.Action(
            androidx.compose.material.icons.filled.Close.toString().toInt(),
            "Cancel",
            PendingIntent.getService(this, downloadId.toInt(), cancelIntent, PendingIntent.FLAG_IMMUTABLE)
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading ${entity.appName}")
            .setContentText("$downloadedStr / $totalStr ($progress%)")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(pauseAction)
            .addAction(cancelAction)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BASE + downloadId.toInt(), notification)
    }

    private fun showCompletionNotification(downloadId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = downloadDao.getById(downloadId).first() ?: return@launch

            val installIntent = Intent(this@DownloadService, DownloadService::class.java).apply {
                action = ACTION_INSTALL
                putExtra("download_id", downloadId)
            }

            val installAction = NotificationCompat.Action(
                androidx.compose.material.icons.filed.Install.toString().toInt(),
                "Install",
                PendingIntent.getService(this@DownloadService, downloadId.toInt(), installIntent, PendingIntent.FLAG_IMMUTABLE)
            )

            val notification = NotificationCompat.Builder(this@DownloadService, CHANNEL_ID)
                .setContentTitle("${entity.appName} downloaded")
                .setContentText("Tap to install")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .addAction(installAction)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .build()

            notificationManager.notify(NOTIFICATION_ID_BASE + downloadId.toInt(), notification)
        }
    }

    private fun showErrorNotification(downloadId: Long, error: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Download failed")
            .setContentText(error)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ERROR)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BASE + downloadId.toInt(), notification)
    }

    fun pauseDownload(downloadId: Long) {
        downloadJobs[downloadId]?.cancel()
        updateStatus(downloadId, DownloadStatus.PAUSED)
    }

    fun resumeDownload(downloadId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val entity = downloadDao.getById(downloadId).first() ?: return@launch
            if (entity.status == DownloadStatus.PAUSED || entity.status == DownloadStatus.FAILED) {
                val file = File(entity.localPath ?: "")
                downloadFile(downloadId, entity.downloadUrl, file, entity.totalBytes)
            }
        }
    }

    fun cancelDownload(downloadId: Long) {
        downloadJobs[downloadId]?.cancel()
        downloadJobs.remove(downloadId)
        notificationManager.cancel(NOTIFICATION_ID_BASE + downloadId.toInt())
        CoroutineScope(Dispatchers.IO).launch {
            downloadDao.deleteById(downloadId)
        }
    }

    fun installApk(localPath: String) {
        val file = File(localPath)
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            data = uri
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            putExtra(Intent.EXTRA_RETURN_RESULT, true)
        }

        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            val downloadId = intent.getLongExtra("download_id", -1)
            when (action) {
                ACTION_PAUSE -> pauseDownload(downloadId)
                ACTION_CANCEL -> cancelDownload(downloadId)
                ACTION_INSTALL -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val entity = downloadDao.getById(downloadId).first()
                        entity?.localPath?.let { installApk(it) }
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        downloadJobs.values.forEach { it.cancel() }
        downloadJobs.clear()
        super.onDestroy()
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1_000_000_000 -> String.format("%.1f GB", bytes / 1_000_000_000.0)
            bytes >= 1_000_000 -> String.format("%.1f MB", bytes / 1_000_000.0)
            bytes >= 1_000 -> String.format("%.1f KB", bytes / 1_000.0)
            else -> "$bytes B"
        }
    }

    private fun formatSpeed(bytesPerSecond: Long): String {
        return when {
            bytesPerSecond >= 1_000_000 -> String.format("%.1f MB/s", bytesPerSecond / 1_000_000.0)
            bytesPerSecond >= 1_000 -> String.format("%.1f KB/s", bytesPerSecond / 1_000.0)
            else -> "$bytesPerSecond B/s"
        }
    }
}