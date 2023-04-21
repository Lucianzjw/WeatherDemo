package com.example.weatherdemo.ui.fra

import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.example.weatherdemo.base.BaseMVVMFra
import com.example.weatherdemo.databinding.FraWeatherBinding
import com.example.weatherdemo.utils.MagicCons.CITYCODE
import com.example.weatherdemo.utils.UIUtils.dp2px
import com.example.weatherdemo.view.magicindicator.buildins.UIUtil
import com.example.weatherdemo.vm.WeatherFraVm

class WeatherFra :BaseMVVMFra<FraWeatherBinding,WeatherFraVm>(){

    //城市code
    private var cityCode :String?= null

    override fun initViewModel(): WeatherFraVm {
        return getFragmentScopeViewModel(WeatherFraVm::class.java)
    }

    override fun initViewBinding(): FraWeatherBinding {
        return FraWeatherBinding.inflate(layoutInflater)
    }

    override fun observableLiveData() {
        super.observableLiveData()
        mViewModel.apiResult.observe(this){

            val casts = it?.forecasts?.get(0)?.casts
            if (casts?.isNotEmpty() == true &&casts.size >1){
                it.forecasts?.get(0)?.casts?.get(1)?.apply {
                    mBinding.textCity.text = it.forecasts?.get(0)?.city
                    mBinding.textData.text = "明天天气(${date})"
                    mBinding.textDayTempTomorrow.text =  "白天温度：${daytemp}"
                    mBinding.textNightTempTomorrow.text = "晚上温度：${nighttemp}"
                    mBinding.textDayWindyTomorrow.text = "白天风向：${daywind}"
                    mBinding.textNightWindyTomorrow.text = "晚上风向：${daywind}"
                    mBinding.textDayPowerTomorrow.text = "白天风力：${daypower}"
                    mBinding.textNightPowerTomorrow.text = "晚上风力：${nightpower}"
                    mBinding.textDayWeatherTomorrow.text = "白天天气气象：${dayweather}"
                    mBinding.textNightWeatherTomorrow.text = "晚上天气气象：${nightweather}"
                    showDayBg(getWeatherIndex(dayweather))

                }
            }

        }
    }

    override fun setUpData() {
        cityCode = arguments?.getString(CITYCODE)
        cityCode?.let { mViewModel.weatherInfo(it) }

    }

    override fun setUpView() {

    }

    override fun setUpListener() {

    }

    /**
     * 显示动画
     */
    private fun showDayBg(index: Int) {
        mBinding.lottieView.imageAssetsFolder = "images"
        when(index){
            1->{
                //晴天
                mBinding.lottieView.setAnimation("qing_d.json")
            }
            2->{
                //阴天
                mBinding.lottieView.setAnimation("yintian.json")
            }
            3->{
                //沙尘
                mBinding.lottieView.setAnimation("shachen.json")
            }
            4->{
                //多云
                mBinding.lottieView.setAnimation("duoyun_d.json")
                val layoutParams = mBinding.lottieView.layoutParams
                layoutParams.height = dp2px(context, 180F)
                mBinding.lottieView.setLayoutParams(layoutParams)
            }
            5->{
                //小雨
                mBinding.lottieView.setAnimation("yu_xiao.json")
            }
            6->{
                //雾霾
                mBinding.lottieView.setAnimation("wumai.json")
            }
            else->{
                mBinding.lottieView.setAnimation("qing_d.json")
            }
        }

        mBinding.lottieView.playAnimation()
    }

    companion object{
        @JvmStatic
        fun newInstance(code:String?): WeatherFra {
            val args = Bundle()
            args.putString(CITYCODE,code)
            val fragment = WeatherFra()
            fragment.arguments = args
            return fragment
        }
    }

    private fun getWeatherIndex(weather:String):Int{
        if (weather.contains("晴")){
            return 1
        }
        if (weather.contains("阴")){
            return 2
        }
        if (weather.contains("沙")){
            return 3
        }
        if (weather.contains("多云")){
            return 4
        }
        if (weather.contains("雨")){
            return 5
        }
        if (weather.contains("霾")){
            return 6
        }
        return 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.lottieView.cancelAnimation()
    }

}