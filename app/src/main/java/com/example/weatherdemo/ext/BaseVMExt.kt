package com.example.weatherdemo.ext

import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.LogUtils

import com.example.weatherdemo.base.BaseVM
import com.example.weatherdemo.model.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * BaseViewModel的一些扩展方法
 *
 */

/**
 * 启动协程，封装了viewModelScope.launch
 *
 * @param tryBlock try语句运行的函数
 * @param finallyBlock finally语句运行的函数，可以用来做一些资源回收等，默认空实现
 */
fun BaseVM.launch(
    tryBlock: suspend CoroutineScope.() -> Unit,
    finallyBlock: suspend CoroutineScope.() -> Unit = {}
): Job {
    // 默认是执行在主线程，相当于launch(Dispatchers.Main)
    return viewModelScope.launch {
        try {
            tryBlock()
        } catch (e: Exception) {

        } finally {
            finallyBlock()
        }
    }
}


/**
 * 请求结果处理
 *
 * @param response ApiResponse
 * @param successBlock 服务器请求成功返回成功码的执行回调，默认空实现
 */
suspend fun BaseVM.handleRequest(
    response: ApiResponse,
    successBlock: suspend CoroutineScope.(response: ApiResponse) -> Unit = {},
) {
    coroutineScope {
        LogUtils.e("接口响应结果：",JSON.toJSON(response))
        when (response.infocode) {
            "10000" -> successBlock(response)// 服务器返回请求成功码

            else -> { // 服务器返回的其他错误码

            }
        }
    }
}

