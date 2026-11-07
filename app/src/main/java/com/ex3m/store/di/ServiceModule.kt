package com.ex3m.store.di

import android.content.Context
import com.ex3m.store.service.DownloadService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideDownloadService(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): DownloadService {
        // This is a workaround - in reality DownloadService is a Service, not a regular class
        // We'll use a different approach for the download service
        return DownloadService()
    }

    @Provides
    @Singleton
    fun provideOkHttpClientForDownload(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }
}