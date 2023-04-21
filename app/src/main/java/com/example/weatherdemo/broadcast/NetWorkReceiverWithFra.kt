package com.example.weatherdemo.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.example.weatherdemo.base.BaseMVVMFra
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class NetWorkReceiverWithFra(val fra: BaseMVVMFra<*, *>) : BroadcastReceiver() {
    val mFra = fra

    override fun onReceive(context: Context?, intent: Intent?) {
        Observable.just(1)
            .map { integer: Int? -> NetworkUtils.isAvailable() }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { aBoolean: Boolean ->
                if (aBoolean) {
                    LogUtils.e("网络状态：可用");
                    mFra.onNetworkStatus(true)
                } else {
                    LogUtils.e("网络状态：不可用");
                    mFra.onNetworkStatus(false)
                }
            }
    }

}