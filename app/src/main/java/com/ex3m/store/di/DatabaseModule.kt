package com.ex3m.store.di

import android.content.Context
import androidx.room.Room
import com.ex3m.store.data.db.DownloadDatabase
import com.ex3m.store.data.db.DownloadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): DownloadDatabase {
        return Room.databaseBuilder(
            context,
            DownloadDatabase::class.java,
            "downloads-db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDownloadDao(database: DownloadDatabase): DownloadDao {
        return database.downloadDao()
    }
}