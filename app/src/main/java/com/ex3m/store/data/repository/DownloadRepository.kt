package com.ex3m.store.data.repository

import com.ex3m.store.data.db.DownloadDao
import com.ex3m.store.data.model.DownloadEntity
import com.ex3m.store.data.model.DownloadStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    private val downloadDao: DownloadDao
) {
    fun getAllDownloads(): Flow<List<DownloadEntity>> = downloadDao.getAll()

    fun getActiveDownloads(): Flow<List<DownloadEntity>> = downloadDao.getByStatuses(
        listOf(DownloadStatus.PENDING, DownloadStatus.DOWNLOADING, DownloadStatus.PAUSED)
    )

    fun getCompletedDownloads(): Flow<List<DownloadEntity>> = downloadDao.getByStatuses(
        listOf(DownloadStatus.COMPLETED, DownloadStatus.INSTALLED)
    )

    suspend fun addDownload(entity: DownloadEntity): Long = downloadDao.insert(entity)

    suspend fun updateDownload(entity: DownloadEntity) = downloadDao.update(entity)

    suspend fun deleteDownload(id: Long) = downloadDao.deleteById(id)

    suspend fun clearAll() = downloadDao.deleteAll()
}