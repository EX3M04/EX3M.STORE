package com.ex3m.store.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ex3m.store.data.model.DownloadEntity
import com.ex3m.store.data.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(download: DownloadEntity): Long

    @Update
    suspend fun update(download: DownloadEntity): Int

    @Query("SELECT * FROM downloads WHERE id = :id")
    fun getById(id: Long): Flow<DownloadEntity?>

    @Query("SELECT * FROM downloads WHERE downloadUrl = :url")
    fun getByUrl(url: String): Flow<DownloadEntity?>

    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun getAll(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status IN (:statuses) ORDER BY createdAt DESC")
    fun getByStatuses(statuses: List<DownloadStatus>): Flow<List<DownloadEntity>>

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM downloads")
    suspend fun deleteAll(): Int
}