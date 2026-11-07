package com.ex3m.store.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ex3m.store.data.model.AppCard
import com.ex3m.store.data.repository.AppRepository
import com.ex3m.store.data.repository.DownloadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val downloadRepository: DownloadRepository,
    private val settingsPreferences: com.ex3m.store.data.prefs.SettingsPreferences
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(MainTab.STORE)
    val selectedTab: StateFlow<MainTab> = _selectedTab

    val apps = appRepository.apps
    val isLoading = appRepository.isLoading
    val error = appRepository.error

    val activeDownloadsCount = downloadRepository.getActiveDownloads()
        .map { it.count { it.status != com.ex3m.store.data.model.DownloadStatus.COMPLETED && it.status != com.ex3m.store.data.model.DownloadStatus.INSTALLED } }

    fun onTabSelected(tab: MainTab) {
        _selectedTab.value = tab
        if (tab == MainTab.STORE) {
            loadApps()
        }
    }

    fun loadApps() {
        viewModelScope.launch {
            settingsPreferences.showPrereleaseApps().firstOrNull().let { showPrerelease ->
                appRepository.fetchApps(showPrerelease ?: false)
            }
        }
    }

    fun retryLoad() {
        loadApps()
    }
}

enum class MainTab {
    STORE,
    DOWNLOADS,
    SETTINGS
}