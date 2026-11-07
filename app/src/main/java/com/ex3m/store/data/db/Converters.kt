package com.ex3m.store.data.db

import androidx.room.TypeConverter
import com.ex3m.store.data.model.DownloadStatus

class Converters {
    @TypeConverter
    fun fromDownloadStatus(status: DownloadStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toDownloadStatus(status: String?): DownloadStatus? {
        return status?.let { DownloadStatus.valueOf(it) }
    }
}