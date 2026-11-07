package com.ex3m.store.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesKeys
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava3.RxDataStore
import com.ex3m.store.data.model.DownloadStatus
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPreferences @Inject constructor(
    private val context: Context
) {
    private val dataStore: RxDataStore<Preferences> = RxPreferenceDataStoreBuilder(context, "settings").build()

    // Theme settings
    private val THEME_MODE = preferencesKey<Int>("theme_mode")
    private val USE_DYNAMIC_COLORS = preferencesKey<Boolean>("use_dynamic_colors")
    private val CUSTOM_PRIMARY_COLOR = preferencesKey<Long>("custom_primary_color")
    private val CUSTOM_SECONDARY_COLOR = preferencesKey<Long>("custom_secondary_color")
    private val CUSTOM_TERTIARY_COLOR = preferencesKey<Long>("custom_tertiary_color")

    // UI Customization
    private val CARD_ELEVATION = preferencesKey<Float>("card_elevation")
    private val CARD_CORNER_RADIUS = preferencesKey<Float>("card_corner_radius")
    private val CARD_SCALE_ON_PRESS = preferencesKey<Boolean>("card_scale_on_press")
    private val ANIMATION_SPEED = preferencesKey<Float>("animation_speed")
    private val SHOW_APP_VERSION_ON_CARD = preferencesKey<Boolean>("show_app_version_on_card")
    private val SHOW_APP_SIZE_ON_CARD = preferencesKey<Boolean>("show_app_size_on_card")
    private val COMPACT_CARD_MODE = preferencesKey<Boolean>("compact_card_mode")

    // Download settings
    private val DOWNLOAD_PATH = preferencesKey<String>("download_path")
    private val MAX_CONCURRENT_DOWNLOADS = preferencesKey<Int>("max_concurrent_downloads")
    private val DOWNLOAD_OVER_WIFI_ONLY = preferencesKey<Boolean>("download_over_wifi_only")
    private val AUTO_INSTALL_AFTER_DOWNLOAD = preferencesKey<Boolean>("auto_install_after_download")
    private val SHOW_DOWNLOAD_NOTIFICATION = preferencesKey<Boolean>("show_download_notification")
    private val DOWNLOAD_CHUNK_SIZE = preferencesKey<Int>("download_chunk_size")
    private val RESUME_DOWNLOADS_ON_STARTUP = preferencesKey<Boolean>("resume_downloads_on_startup")

    // General settings
    private val AUTO_REFRESH_INTERVAL = preferencesKey<Int>("auto_refresh_interval")
    private val SHOW_PRERELEASE_APPS = preferencesKey<Boolean>("show_prerelease_apps")
    private val LANGUAGE = preferencesKey<String>("language")
    private val ENABLE_ANALYTICS = preferencesKey<Boolean>("enable_analytics")
    private val CHECK_UPDATES_ON_START = preferencesKey<Boolean>("check_updates_on_start")

    // Advanced settings
    private val GITHUB_TOKEN = preferencesKey<String>("github_token")
    private val CUSTOM_GITHUB_API_URL = preferencesKey<String>("custom_github_api_url")
    private val ENABLE_DEBUG_LOGGING = preferencesKey<Boolean>("enable_debug_logging")
    private val CACHE_EXPIRY_HOURS = preferencesKey<Int>("cache_expiry_hours")
    private val MAX_CACHE_SIZE_MB = preferencesKey<Int>("max_cache_size_mb")

    // Theme mode constants
    companion object {
        const val THEME_SYSTEM = 0
        const val THEME_LIGHT = 1
        const val THEME_DARK = 2
    }

    // Getters
    fun themeMode(): Flowable<Int> = dataStore.data.map { it[THEME_MODE] ?: THEME_SYSTEM }
    fun useDynamicColors(): Flowable<Boolean> = dataStore.data.map { it[USE_DYNAMIC_COLORS] ?: true }
    fun customPrimaryColor(): Flowable<Long> = dataStore.data.map { it[CUSTOM_PRIMARY_COLOR] ?: 0xFF6750A4L }
    fun customSecondaryColor(): Flowable<Long> = dataStore.data.map { it[CUSTOM_SECONDARY_COLOR] ?: 0FF03DAC5L }
    fun customTertiaryColor(): Flowable<Long> = dataStore.data.map { it[CUSTOM_TERTIARY_COLOR] ?: 0FF018782L }

    fun cardElevation(): Flowable<Float> = dataStore.data.map { it[CARD_ELEVATION] ?: 8f }
    fun cardCornerRadius(): Flowable<Float> = dataStore.data.map { it[CARD_CORNER_RADIUS] ?: 28f }
    fun cardScaleOnPress(): Flowable<Boolean> = dataStore.data.map { it[CARD_SCALE_ON_PRESS] ?: true }
    fun animationSpeed(): Flowable<Float> = dataStore.data.map { it[ANIMATION_SPEED] ?: 1f }
    fun showAppVersionOnCard(): Flowable<Boolean> = dataStore.data.map { it[SHOW_APP_VERSION_ON_CARD] ?: true }
    fun showAppSizeOnCard(): Flowable<Boolean> = dataStore.data.map { it[SHOW_APP_SIZE_ON_CARD] ?: true }
    fun compactCardMode(): Flowable<Boolean> = dataStore.data.map { it[COMPACT_CARD_MODE] ?: false }

    fun downloadPath(): Flowable<String> = dataStore.data.map { it[DOWNLOAD_PATH] ?: context.getExternalFilesDir(null)?.absolutePath ?: "" }
    fun maxConcurrentDownloads(): Flowable<Int> = dataStore.data.map { it[MAX_CONCURRENT_DOWNLOADS] ?: 3 }
    fun downloadOverWifiOnly(): Flowable<Boolean> = dataStore.data.map { it[DOWNLOAD_OVER_WIFI_ONLY] ?: false }
    fun autoInstallAfterDownload(): Flowable<Boolean> = dataStore.data.map { it[AUTO_INSTALL_AFTER_DOWNLOAD] ?: false }
    fun showDownloadNotification(): Flowable<Boolean> = dataStore.data.map { it[SHOW_DOWNLOAD_NOTIFICATION] ?: true }
    fun downloadChunkSize(): Flowable<Int> = dataStore.data.map { it[DOWNLOAD_CHUNK_SIZE] ?: 8192 }
    fun resumeDownloadsOnStartup(): Flowable<Boolean> = dataStore.data.map { it[RESUME_DOWNLOADS_ON_STARTUP] ?: true }

    fun autoRefreshInterval(): Flowable<Int> = dataStore.data.map { it[AUTO_REFRESH_INTERVAL] ?: 30 }
    fun showPrereleaseApps(): Flowable<Boolean> = dataStore.data.map { it[SHOW_PRERELEASE_APPS] ?: false }
    fun language(): Flowable<String> = dataStore.data.map { it[LANGUAGE] ?: "system" }
    fun enableAnalytics(): Flowable<Boolean> = dataStore.data.map { it[ENABLE_ANALYTICS] ?: false }
    fun checkUpdatesOnStart(): Flowable<Boolean> = dataStore.data.map { it[CHECK_UPDATES_ON_START] ?: true }

    fun githubToken(): Flowable<String> = dataStore.data.map { it[GITHUB_TOKEN] ?: "" }
    fun customGithubApiUrl(): Flowable<String> = dataStore.data.map { it[CUSTOM_GITHUB_API_URL] ?: "" }
    fun enableDebugLogging(): Flowable<Boolean> = dataStore.data.map { it[ENABLE_DEBUG_LOGGING] ?: false }
    fun cacheExpiryHours(): Flowable<Int> = dataStore.data.map { it[CACHE_EXPIRY_HOURS] ?: 24 }
    fun maxCacheSizeMb(): Flowable<Int> = dataStore.data.map { it[MAX_CACHE_SIZE_MB] ?: 100 }

    // Setters
    suspend fun setThemeMode(mode: Int) = dataStore.updateData { it[THEME_MODE] = mode }
    suspend fun setUseDynamicColors(enabled: Boolean) = dataStore.updateData { it[USE_DYNAMIC_COLORS] = enabled }
    suspend fun setCustomPrimaryColor(color: Long) = dataStore.updateData { it[CUSTOM_PRIMARY_COLOR] = color }
    suspend fun setCustomSecondaryColor(color: Long) = dataStore.updateData { it[CUSTOM_SECONDARY_COLOR] = color }
    suspend fun setCustomTertiaryColor(color: Long) = dataStore.updateData { it[CUSTOM_TERTIARY_COLOR] = color }

    suspend fun setCardElevation(elevation: Float) = dataStore.updateData { it[CARD_ELEVATION] = elevation }
    suspend fun setCardCornerRadius(radius: Float) = dataStore.updateData { it[CARD_CORNER_RADIUS] = radius }
    suspend fun setCardScaleOnPress(enabled: Boolean) = dataStore.updateData { it[CARD_SCALE_ON_PRESS] = enabled }
    suspend fun setAnimationSpeed(speed: Float) = dataStore.updateData { it[ANIMATION_SPEED] = speed }
    suspend fun setShowAppVersionOnCard(show: Boolean) = dataStore.updateData { it[SHOW_APP_VERSION_ON_CARD] = show }
    suspend fun setShowAppSizeOnCard(show: Boolean) = dataStore.updateData { it[SHOW_APP_SIZE_ON_CARD] = show }
    suspend fun setCompactCardMode(enabled: Boolean) = dataStore.updateData { it[COMPACT_CARD_MODE] = enabled }

    suspend fun setDownloadPath(path: String) = dataStore.updateData { it[DOWNLOAD_PATH] = path }
    suspend fun setMaxConcurrentDownloads(count: Int) = dataStore.updateData { it[MAX_CONCURRENT_DOWNLOADS] = count }
    suspend fun setDownloadOverWifiOnly(enabled: Boolean) = dataStore.updateData { it[DOWNLOAD_OVER_WIFI_ONLY] = enabled }
    suspend fun setAutoInstallAfterDownload(enabled: Boolean) = dataStore.updateData { it[AUTO_INSTALL_AFTER_DOWNLOAD] = enabled }
    suspend fun setShowDownloadNotification(enabled: Boolean) = dataStore.updateData { it[SHOW_DOWNLOAD_NOTIFICATION] = enabled }
    suspend fun setDownloadChunkSize(size: Int) = dataStore.updateData { it[DOWNLOAD_CHUNK_SIZE] = size }
    suspend fun setResumeDownloadsOnStartup(enabled: Boolean) = dataStore.updateData { it[RESUME_DOWNLOADS_ON_STARTUP] = enabled }

    suspend fun setAutoRefreshInterval(minutes: Int) = dataStore.updateData { it[AUTO_REFRESH_INTERVAL] = minutes }
    suspend fun setShowPrereleaseApps(show: Boolean) = dataStore.updateData { it[SHOW_PRERELEASE_APPS] = show }
    suspend fun setLanguage(lang: String) = dataStore.updateData { it[LANGUAGE] = lang }
    suspend fun setEnableAnalytics(enabled: Boolean) = dataStore.updateData { it[ENABLE_ANALYTICS] = enabled }
    suspend fun setCheckUpdatesOnStart(enabled: Boolean) = dataStore.updateData { it[CHECK_UPDATES_ON_START] = enabled }

    suspend fun setGithubToken(token: String) = dataStore.updateData { it[GITHUB_TOKEN] = token }
    suspend fun setCustomGithubApiUrl(url: String) = dataStore.updateData { it[CUSTOM_GITHUB_API_URL] = url }
    suspend fun setEnableDebugLogging(enabled: Boolean) = dataStore.updateData { it[ENABLE_DEBUG_LOGGING] = enabled }
    suspend fun setCacheExpiryHours(hours: Int) = dataStore.updateData { it[CACHE_EXPIRY_HOURS] = hours }
    suspend fun setMaxCacheSizeMb(mb: Int) = dataStore.updateData { it[MAX_CACHE_SIZE_MB] = mb }
}