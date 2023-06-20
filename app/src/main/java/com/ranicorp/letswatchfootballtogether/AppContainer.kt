package com.ranicorp.letswatchfootballtogether

import com.ranicorp.letswatchfootballtogether.data.source.remote.ApiClient

class AppContainer {

    private var apiClient: ApiClient? = null

    fun provideApiClient(): ApiClient {
        return apiClient ?: ApiClient.create().apply {
            apiClient = this
        }
    }
}