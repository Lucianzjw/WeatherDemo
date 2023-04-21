package com.example.weatherdemo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.NetworkUtils;
import com.example.weatherdemo.base.BaseMVVMAc;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NetWorkStateWithAcBR extends BroadcastReceiver {

    private BaseMVVMAc activity;
    private final String TAG = "activity-->>NetWorkStateWithAcBR";

    public NetWorkStateWithAcBR(BaseMVVMAc activity) {
        this.activity = activity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Observable.just(1)
                .map(integer -> NetworkUtils.isAvailable())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (activity == null) {
                        return;
                    }

                    if (aBoolean) {
//                            LogUtils.e(TAG, "网络状态：可用");
                        activity.onNetworkStatus(true);
                    } else {
//                            LogUtils.e(TAG, "网络状态：不可用");
                        activity.onNetworkStatus(false);
                    }
                });

    }


    public void onDestroy() {
        if (activity != null) {
            activity = null;
        }
    }
}