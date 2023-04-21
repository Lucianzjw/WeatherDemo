package com.example.weatherdemo.utils;

/**
 * 用于管理fragment的可视化周期
 * */
public interface ISupportFragment {

    /**
     * fragment显示在屏幕中时回调此函数
     * */
    void onSupportVisible();

    /**
     * fragment隐藏时中时回调此函数
     * */
    void onSupportInVisible();

    /**
     * 懒加载
     *
     * 只会调用一次，在onSupportVisible前调用
     * */
    void onLazyInited();
    
    boolean isSupportVisible();

    void dispatchUserVisibleHint(boolean visible);
}
