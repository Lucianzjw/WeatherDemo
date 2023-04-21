package com.benhu.base.http

import com.example.weatherdemo.model.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository数据仓库基类，主要用于协程的调用
 */
open class BaseRepository {

    suspend fun <T> apiCall(api: suspend () -> ApiResponse): ApiResponse {
        return withContext(Dispatchers.IO) { api.invoke() }
    }
}