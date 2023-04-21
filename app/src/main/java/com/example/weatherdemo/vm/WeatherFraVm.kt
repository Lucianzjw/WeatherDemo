package com.example.weatherdemo.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.LogUtils
import com.example.weatherdemo.base.BaseVM
import com.example.weatherdemo.ext.handleRequest
import com.example.weatherdemo.ext.launch
import com.example.weatherdemo.model.ApiResponse
import com.example.weatherdemo.net.WeatherRepository
import com.example.weatherdemo.utils.MagicCons.APPKEY
import com.example.weatherdemo.utils.MagicCons.EXTENSIONS
import com.example.weatherdemo.utils.MagicCons.output

class WeatherFraVm(application: Application) : BaseVM(application){

    // 天气数据
    private val _apiResult = MutableLiveData<ApiResponse?>()
    val apiResult: MutableLiveData<ApiResponse?> = _apiResult


    fun weatherInfo(cityCode:String) {
        launch({
            handleRequest(WeatherRepository.weatherInfo(APPKEY,cityCode, EXTENSIONS)) {
                _apiResult.value = it
            }
        })

    }


}