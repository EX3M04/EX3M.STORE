package com.ex3m.store.data.repository

import com.ex3m.store.data.model.AppCard
import com.ex3m.store.data.model.GitHubRelease
import com.ex3m.store.service.GitHubApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.sharingStarted
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val apiService: GitHubApiService
) {
    private val _apps = MutableStateFlow<List<AppCard>>(emptyList())
    val apps: Flow<List<AppCard>> = _apps
        .stateIn(
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading
        .stateIn(
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error
        .stateIn(
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    suspend fun fetchApps(showPrerelease: Boolean = false) {
        _isLoading.value = true
        _error.value = null
        try {
            val releases = apiService.getReleases(
                GitHubApiService.OWNER,
                GitHubApiService.REPO
            )

            val appCards = releases
                .filter { release ->
                    !release.draft && (showPrerelease || !release.prerelease)
                }
                .map { release ->
                    val iconName = "${release.appName.replace(" ", "").lowercase()}.jpg"
                    val iconUrl = "https://raw.githubusercontent.com/${GitHubApiService.OWNER}/${GitHubApiService.REPO}/main/${iconName}"
                    val descriptionFileName = "${release.appName.replace(" ", "")}.txt"
                    val descriptionUrl = "https://raw.githubusercontent.com/${GitHubApiService.OWNER}/${GitHubApiService.REPO}/main/descriptions/${descriptionFileName}"

                    AppCard(
                        release = release,
                        iconUrl = iconUrl,
                        description = "" // Will be fetched separately
                    )
                }

            _apps.value = appCards
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to fetch apps"
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun fetchDescription(appName: String): String {
        val descriptionFileName = "${appName.replace(" ", "")}.txt"
        val url = "https://raw.githubusercontent.com/${GitHubApiService.OWNER}/${GitHubApiService.REPO}/main/descriptions/${descriptionFileName}"
        // This would need an additional HTTP call - simplified for now
        return ""
    }
}