package com.example.weatherdemo.ui.ac

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherdemo.R
import com.example.weatherdemo.base.BaseMVVMAc
import com.example.weatherdemo.databinding.AcMainBinding
import com.example.weatherdemo.ui.fra.WeatherFra
import com.example.weatherdemo.view.magicindicator.ViewPagerHelper
import com.example.weatherdemo.view.magicindicator.buildins.commonnavigator.CommonNavigator
import com.example.weatherdemo.view.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import com.example.weatherdemo.view.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import com.example.weatherdemo.view.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import com.example.weatherdemo.view.magicindicator.buildins.commonnavigator.indicators.BHLinePagerIndicator
import com.example.weatherdemo.view.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import com.example.weatherdemo.view.magicindicator.title.ScaleTransitionPagerTitleView
import com.example.weatherdemo.vm.MainVm

class MainAc :BaseMVVMAc<AcMainBinding,MainVm>(){

    var mNames = mutableListOf("北京", "上海", "广州", "深圳", "苏州", "沈阳")
    var mCodes = mutableListOf("110000", "310000", "440100", "440300", "320500", "210100")


    override fun initViewModel(): MainVm {
        return getActivityScopeViewModel(MainVm::class.java)
    }

    override fun initViewBinding(): AcMainBinding {
        return AcMainBinding.inflate(layoutInflater)
    }

    override fun setUpData() {

    }

    override fun setUpView() {
        mViewPagerAdapter = FragmentAdapter(this)
        mBinding.vp.adapter = mViewPagerAdapter
        mViewPagerAdapter.setData(mCodes)
    }

    override fun setUpListener() {

    }

    private inner class FragmentAdapter(ac: FragmentActivity) :
        FragmentStateAdapter(ac) {

        private var codeList: MutableList<String>? = null

        fun getData() = codeList

        fun setData(data: MutableList<String>) {
            codeList = data
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return codeList?.size ?: 0
        }

        override fun createFragment(position: Int): Fragment {
            return initFraList()[position]
        }

    }

    private lateinit var mViewPagerAdapter: FragmentAdapter

    private fun initFraList():MutableList<Fragment>{
        val fragmentList:MutableList<Fragment> = mutableListOf()
        mCodes.forEachIndexed { index, s ->
            fragmentList.add(WeatherFra.newInstance(s))
        }

        return fragmentList
    }


}