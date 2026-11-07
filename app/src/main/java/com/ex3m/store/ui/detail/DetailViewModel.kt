package com.ex3m.store.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ex3m.store.data.model.AppCard
import com.ex3m.store.data.model.DownloadEntity
import com.ex3m.store.data.model.DownloadStatus
import com.ex3m.store.data.repository.DownloadRepository
import com.ex3m.store.service.DownloadService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val downloadService: DownloadService
) : ViewModel() {

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    private val _downloadProgress = MutableStateFlow<DownloadProgress?>(null)
    val downloadProgress: StateFlow<DownloadProgress?> = _downloadProgress

    data class DownloadState(
        val status: DownloadStatus = DownloadStatus.PENDING,
        val progress: Float = 0f,
        val downloadedBytes: Long = 0,
        val totalBytes: Long = 0,
        val speed: String = "",
        val error: String? = null,
        val localPath: String? = null
    ) {
        companion object {
            val Idle = DownloadState()
        }
    }

    data class DownloadProgress(
        val progress: Float,
        val downloadedBytes: Long,
        val totalBytes: Long,
        val speed: String,
        val status: DownloadStatus
    )

    fun startDownload(app: AppCard) {
        viewModelScope.launch {
            _downloadState.value = DownloadState(status = DownloadStatus.DOWNLOADING)
            downloadService.startDownload(app)
        }
    }

    fun pauseDownload(downloadId: Long) {
        downloadService.pauseDownload(downloadId)
    }

    fun resumeDownload(downloadId: Long) {
        downloadService.resumeDownload(downloadId)
    }

    fun cancelDownload(downloadId: Long) {
        downloadService.cancelDownload(downloadId)
        _downloadState.value = DownloadState.Idle
    }

    fun installApk(localPath: String) {
        downloadService.installApk(localPath)
    }

    fun observeDownload(downloadId: Long) {
        viewModelScope.launch {
            downloadRepository.getById(downloadId).collect { entity ->
                entity?.let {
                    _downloadState.value = DownloadState(
                        status = it.status,
                        progress = it.progress,
                        downloadedBytes = it.downloadedBytes,
                        totalBytes = it.totalBytes,
                        localPath = it.localPath
                    )
                }
            }
        }
    }
}