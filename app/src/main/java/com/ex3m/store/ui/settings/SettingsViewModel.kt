package com.ex3m.store.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ex3m.store.data.prefs.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    // Theme settings
    private val _themeMode = MutableStateFlow(0)
    val themeMode: Flow<Int> = settingsPreferences.themeMode()
        .onStart { _themeMode.value = it.first() }
        .map { _themeMode.value = it; it }
        .asStateFlow()

    private val _useDynamicColors = MutableStateFlow(true)
    val useDynamicColors: Flow<Boolean> = settingsPreferences.useDynamicColors()
        .onStart { _useDynamicColors.value = it.first() }
        .map { _useDynamicColors.value = it; it }
        .asStateFlow()

    // UI Customization
    private val _cardElevation = MutableStateFlow(8f)
    val cardElevation: Flow<Float> = settingsPreferences.cardElevation()
        .onStart { _cardElevation.value = it.first() }
        .map { _cardElevation.value = it; it }
        .asStateFlow()

    private val _cardCornerRadius = MutableStateFlow(28f)
    val cardCornerRadius: Flow<Float> = settingsPreferences.cardCornerRadius()
        .onStart { _cardCornerRadius.value = it.first() }
        .map { _cardCornerRadius.value = it; it }
        .asStateFlow()

    private val _cardScaleOnPress = MutableStateFlow(true)
    val cardScaleOnPress: Flow<Boolean> = settingsPreferences.cardScaleOnPress()
        .onStart { _cardScaleOnPress.value = it.first() }
        .map { _cardScaleOnPress.value = it; it }
        .asStateFlow()

    private val _animationSpeed = MutableStateFlow(1f)
    val animationSpeed: Flow<Float> = settingsPreferences.animationSpeed()
        .onStart { _animationSpeed.value = it.first() }
        .map { _animationSpeed.value = it; it }
        .asStateFlow()

    private val _showAppVersionOnCard = MutableStateFlow(true)
    val showAppVersionOnCard: Flow<Boolean> = settingsPreferences.showAppVersionOnCard()
        .onStart { _showAppVersionOnCard.value = it.first() }
        .map { _showAppVersionOnCard.value = it; it }
        .asStateFlow()

    private val _showAppSizeOnCard = MutableStateFlow(true)
    val showAppSizeOnCard: Flow<Boolean> = settingsPreferences.showAppSizeOnCard()
        .onStart { _showAppSizeOnCard.value = it.first() }
        .map { _showAppSizeOnCard.value = it; it }
        .asStateFlow()

    private val _compactCardMode = MutableStateFlow(false)
    val compactCardMode: Flow<Boolean> = settingsPreferences.compactCardMode()
        .onStart { _compactCardMode.value = it.first() }
        .map { _compactCardMode.value = it; it }
        .asStateFlow()

    // Download settings
    private val _downloadOverWifiOnly = MutableStateFlow(false)
    val downloadOverWifiOnly: Flow<Boolean> = settingsPreferences.downloadOverWifiOnly()
        .onStart { _downloadOverWifiOnly.value = it.first() }
        .map { _downloadOverWifiOnly.value = it; it }
        .asStateFlow()

    private val _autoInstallAfterDownload = MutableStateFlow(false)
    val autoInstallAfterDownload: Flow<Boolean> = settingsPreferences.autoInstallAfterDownload()
        .onStart { _autoInstallAfterDownload.value = it.first() }
        .map { _autoInstallAfterDownload.value = it; it }
        .asStateFlow()

    private val _showDownloadNotification = MutableStateFlow(true)
    val showDownloadNotification: Flow<Boolean> = settingsPreferences.showDownloadNotification()
        .onStart { _showDownloadNotification.value = it.first() }
        .map { _showDownloadNotification.value = it; it }
        .asStateFlow()

    private val _maxConcurrentDownloads = MutableStateFlow(3)
    val maxConcurrentDownloads: Flow<Int> = settingsPreferences.maxConcurrentDownloads()
        .onStart { _maxConcurrentDownloads.value = it.first() }
        .map { _maxConcurrentDownloads.value = it; it }
        .asStateFlow()

    // Store settings
    private val _showPrereleaseApps = MutableStateFlow(false)
    val showPrereleaseApps: Flow<Boolean> = settingsPreferences.showPrereleaseApps()
        .onStart { _showPrereleaseApps.value = it.first() }
        .map { _showPrereleaseApps.value = it; it }
        .asStateFlow()

    private val _autoRefreshInterval = MutableStateFlow(30)
    val autoRefreshInterval: Flow<Int> = settingsPreferences.autoRefreshInterval()
        .onStart { _autoRefreshInterval.value = it.first() }
        .map { _autoRefreshInterval.value = it; it }
        .asStateFlow()

    private val _enableAnalytics = MutableStateFlow(false)
    val enableAnalytics: Flow<Boolean> = settingsPreferences.enableAnalytics()
        .onStart { _enableAnalytics.value = it.first() }
        .map { _enableAnalytics.value = it; it }
        .asStateFlow()

    // Advanced settings
    private val _enableDebugLogging = MutableStateFlow(false)
    val enableDebugLogging: Flow<Boolean> = settingsPreferences.enableDebugLogging()
        .onStart { _enableDebugLogging.value = it.first() }
        .map { _enableDebugLogging.value = it; it }
        .asStateFlow()

    // Setters
    fun setThemeMode(mode: Int) {
        _themeMode.value = mode
        viewModelScope.launch { settingsPreferences.setThemeMode(mode) }
    }

    fun setUseDynamicColors(enabled: Boolean) {
        _useDynamicColors.value = enabled
        viewModelScope.launch { settingsPreferences.setUseDynamicColors(enabled) }
    }

    fun setCardElevation(elevation: Float) {
        _cardElevation.value = elevation
        viewModelScope.launch { settingsPreferences.setCardElevation(elevation) }
    }

    fun setCardCornerRadius(radius: Float) {
        _cardCornerRadius.value = radius
        viewModelScope.launch { settingsPreferences.setCardCornerRadius(radius) }
    }

    fun setCardScaleOnPress(enabled: Boolean) {
        _cardScaleOnPress.value = enabled
        viewModelScope.launch { settingsPreferences.setCardScaleOnPress(enabled) }
    }

    fun setAnimationSpeed(speed: Float) {
        _animationSpeed.value = speed
        viewModelScope.launch { settingsPreferences.setAnimationSpeed(speed) }
    }

    fun setShowAppVersionOnCard(show: Boolean) {
        _showAppVersionOnCard.value = show
        viewModelScope.launch { settingsPreferences.setShowAppVersionOnCard(show) }
    }

    fun setShowAppSizeOnCard(show: Boolean) {
        _showAppSizeOnCard.value = show
        viewModelScope.launch { settingsPreferences.setShowAppSizeOnCard(show) }
    }

    fun setCompactCardMode(enabled: Boolean) {
        _compactCardMode.value = enabled
        viewModelScope.launch { settingsPreferences.setCompactCardMode(enabled) }
    }

    fun setDownloadOverWifiOnly(enabled: Boolean) {
        _downloadOverWifiOnly.value = enabled
        viewModelScope.launch { settingsPreferences.setDownloadOverWifiOnly(enabled) }
    }

    fun setAutoInstallAfterDownload(enabled: Boolean) {
        _autoInstallAfterDownload.value = enabled
        viewModelScope.launch { settingsPreferences.setAutoInstallAfterDownload(enabled) }
    }

    fun setShowDownloadNotification(enabled: Boolean) {
        _showDownloadNotification.value = enabled
        viewModelScope.launch { settingsPreferences.setShowDownloadNotification(enabled) }
    }

    fun setMaxConcurrentDownloads(count: Int) {
        _maxConcurrentDownloads.value = count
        viewModelScope.launch { settingsPreferences.setMaxConcurrentDownloads(count) }
    }

    fun setShowPrereleaseApps(show: Boolean) {
        _showPrereleaseApps.value = show
        viewModelScope.launch { settingsPreferences.setShowPrereleaseApps(show) }
    }

    fun setAutoRefreshInterval(minutes: Int) {
        _autoRefreshInterval.value = minutes
        viewModelScope.launch { settingsPreferences.setAutoRefreshInterval(minutes) }
    }

    fun setEnableAnalytics(enabled: Boolean) {
        _enableAnalytics.value = enabled
        viewModelScope.launch { settingsPreferences.setEnableAnalytics(enabled) }
    }

    fun setEnableDebugLogging(enabled: Boolean) {
        _enableDebugLogging.value = enabled
        viewModelScope.launch { settingsPreferences.setEnableDebugLogging(enabled) }
    }
}