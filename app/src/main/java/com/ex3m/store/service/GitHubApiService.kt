package com.ex3m.store.service

import com.ex3m.store.data.model.GitHubRelease
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiService {
    @GET("repos/{owner}/{repo}/releases")
    suspend fun getReleases(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): List<GitHubRelease>

    companion object {
        const val BASE_URL = "https://api.github.com/"
        const val OWNER = "EX3Mka"
        const val REPO = "EX3M.STORE"
    }
}