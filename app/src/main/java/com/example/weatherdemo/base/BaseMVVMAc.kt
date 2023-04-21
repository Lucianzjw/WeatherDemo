package com.example.weatherdemo.base

import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.weatherdemo.R
import com.example.weatherdemo.broadcast.NetWorkStateWithAcBR
import com.example.weatherdemo.utils.UIUtils
import com.gyf.immersionbar.ImmersionBar
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity

abstract class BaseMVVMAc<V : ViewBinding, VM : BaseVM> :
    RxAppCompatActivity() {

    private var mNetWorkChangeReceiver: NetWorkStateWithAcBR? = null

    lateinit var mBinding: V
    lateinit var mViewModel: VM

    /**
     * @return 若页面没有指定ViewModel，请返回Application级 BaseViewModel，否则抛空指针
     */
    protected abstract fun initViewModel(): VM

    protected abstract fun initViewBinding(): V

    /**
     * 预置数据，在UI设置之前
     * */
    protected abstract fun setUpData()

    /**
     * View初始化，设置适配器等
     * */
    protected abstract fun setUpView()

    /**
     * View操作监听事件等
     * */
    protected abstract fun setUpListener()

    protected fun initRequestOrientation(): Int {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    protected open fun initToolbar() {}

    protected open fun isRegisterNetReceiver(): Boolean = false

    private var mActivityProvider: ViewModelProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        UIUtils.setNotFullScreenWindowLayoutInDisplayCutout(this.window)
        requestedOrientation = initRequestOrientation()
        mViewModel = initViewModel()
        super.onCreate(savedInstanceState)

        mBinding = initViewBinding()
        setContentView(mBinding.root)

        //网络状态广播注册
        if (isRegisterNetReceiver()) registerNetChangeReceiver()

        setStatusBar()
        setUpData()
        setUpView()
        setUpListener()

    }

    protected fun setStatusBar() {
        if (ImmersionBar.isSupportStatusBarDarkFont()) {
            ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarDarkFont(true)
                .keyboardEnable(true)
                .statusBarColor(R.color.white)
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                .init()
        } else {
            //处理状态栏有透明度
            ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)
                .fitsSystemWindows(true)
                .keyboardEnable(true)
                .statusBarColor(R.color.white)
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                .init()
        }
    }


    private fun registerNetChangeReceiver() {
        try {
            mNetWorkChangeReceiver = NetWorkStateWithAcBR(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(mNetWorkChangeReceiver, intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO tip 2: Jetpack 通过 "工厂模式" 来实现 ViewModel 的作用域可控，
    //目前我们在项目中提供了 Application、Activity、Fragment 三个级别的作用域，
    //值得注意的是，通过不同作用域的 Provider 获得的 ViewModel 实例不是同一个，
    //所以如果 ViewModel 对状态信息的保留不符合预期，可以从这个角度出发去排查 是否眼前的 ViewModel 实例不是目标实例所致。
    //如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/6257931840
    protected fun <T : ViewModel> getActivityScopeViewModel(modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(this@BaseMVVMAc)
        }
        return mActivityProvider!!.get(modelClass)
    }


    /**
     * 当前网络状态
     *
     * @param isWork 当前网络是否可用
     */
    fun onNetworkStatus(isWork: Boolean) {
        if (isRegisterNetReceiver()) {
            if (isWork) {
                //showNetWorkNormal()
            } else {
                //showNetWorkDisable()
            }
        }
    }

    override fun onDestroy() {

        //注销网络监听广播
        mNetWorkChangeReceiver?.let {
            mNetWorkChangeReceiver!!.onDestroy()
            unregisterReceiver(mNetWorkChangeReceiver)
            mNetWorkChangeReceiver = null
        }

        super.onDestroy()
    }
}