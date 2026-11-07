package com.ex3m.store.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ex3m.store.data.model.DownloadEntity
import com.ex3m.store.data.model.DownloadStatus
import com.ex3m.store.data.repository.DownloadRepository
import com.ex3m.store.service.DownloadService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository,
    private val downloadService: DownloadService
) : ViewModel() {

    val downloads = downloadRepository.getAllDownloads()

    private val _isLoading = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isLoading = _isLoading

    fun onActionClick(download: DownloadEntity) {
        viewModelScope.launch {
            when (download.status) {
                DownloadStatus.PENDING,
                DownloadStatus.FAILED -> {
                    downloadService.resumeDownload(download.id)
                }
                DownloadStatus.DOWNLOADING -> {
                    downloadService.pauseDownload(download.id)
                }
                DownloadStatus.PAUSED -> {
                    downloadService.resumeDownload(download.id)
                }
                DownloadStatus.COMPLETED -> {
                    download.localPath?.let { downloadService.installApk(it) }
                }
                DownloadStatus.INSTALLING,
                DownloadStatus.INSTALLED -> {
                    // No action
                }
            }
        }
    }

    fun deleteDownload(downloadId: Long) {
        viewModelScope.launch {
            downloadService.cancelDownload(downloadId)
        }
    }
}