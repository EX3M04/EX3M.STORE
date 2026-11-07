package com.ex3m.store.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ex3m.store.data.model.DownloadEntity
import com.ex3m.store.data.model.DownloadStatus

@Database(entities = [DownloadEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DownloadDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
}