package com.example.weatherdemo.base

import android.app.Activity
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

import com.example.weatherdemo.broadcast.NetWorkReceiverWithFra
import com.example.weatherdemo.utils.ISupportFragment
import com.example.weatherdemo.utils.VisibleDelegate
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import com.trello.rxlifecycle4.components.support.RxFragment

abstract class BaseMVVMFra<V : ViewBinding, VM : BaseVM> : RxFragment(),
    ISupportFragment {
    private val mVisibleDelegate = VisibleDelegate(this)
    private var mFragmentProvider: ViewModelProvider? = null
    private var mActivityProvider: ViewModelProvider? = null
    lateinit var mBinding: V
    lateinit var mViewModel: VM
    protected var mActivity: RxAppCompatActivity? = null
    protected abstract fun initViewModel(): VM
    private var mNetWorkChangeReceiver: NetWorkReceiverWithFra? = null

    protected abstract fun initViewBinding(): V

    override fun onSupportVisible() {}
    override fun onSupportInVisible() {}
    override fun onLazyInited() {}
    override fun isSupportVisible(): Boolean {
        return false
    }


    override fun dispatchUserVisibleHint(visible: Boolean) {
        if (visible) {
            onLazyInited()
        } else {
            onSupportInVisible()
        }
    }
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = activity as RxAppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mVisibleDelegate.onActivityCreated(savedInstanceState)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mVisibleDelegate.onHiddenChanged(hidden)
    }

    override fun onResume() {
        super.onResume()
        mVisibleDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        mVisibleDelegate.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = initViewBinding()
        if (isRegisterNetReceiver) {
            registerNetChangeReceiver()
        }
        mViewModel = initViewModel()
        setUpData()
        setUpView()
        setUpListener()
        observableLiveData()
        return mBinding.root

    }


    //TODO tip 2: Jetpack 通过 "工厂模式" 来实现 ViewModel 的作用域可控，
    //目前我们在项目中提供了 Application、Activity、Fragment 三个级别的作用域，
    //值得注意的是，通过不同作用域的 Provider 获得的 ViewModel 实例不是同一个，
    //所以如果 ViewModel 对状态信息的保留不符合预期，可以从这个角度出发去排查 是否眼前的 ViewModel 实例不是目标实例所致。
    //如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/6257931840
    fun <T : ViewModel> getFragmentScopeViewModel(modelClass: Class<T>): T {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!!.get(modelClass)
    }

    protected fun <T : ViewModel> getActivityScopeViewModel(modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(mActivity!!)
        }
        return mActivityProvider!!.get(modelClass)
    }

    protected abstract fun setUpData()
    protected abstract fun setUpView()
    protected abstract fun setUpListener()


    protected open val isRegisterNetReceiver: Boolean
        protected get() = false

    override fun onDestroyView() {
        super.onDestroyView()
    }

    protected open fun observableLiveData() {

    }

    private fun registerNetChangeReceiver() {
        try {
            mNetWorkChangeReceiver = NetWorkReceiverWithFra(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            requireActivity().registerReceiver(mNetWorkChangeReceiver, intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 当前网络状态
     *
     * @param isWork 当前网络是否可用
     */
    open fun onNetworkStatus(isWork: Boolean) {

    }

    override fun onDetach() {
        super.onDetach()
        //注销网络监听广播
        mNetWorkChangeReceiver?.let {
            requireActivity().unregisterReceiver(it)
            mNetWorkChangeReceiver = null
        }
    }
}