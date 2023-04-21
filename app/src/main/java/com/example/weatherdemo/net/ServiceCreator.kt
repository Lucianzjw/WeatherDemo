package com.example.weatherdemo.net

import androidx.multidex.BuildConfig
import com.example.weatherdemo.net.WeatherUrl.baseCityUrl
import com.example.weatherdemo.net.converter.FastJsonConverterFactory
import com.example.weatherdemo.net.converter.LiveDataCallAdapterFactory
import com.example.weatherdemo.net.interceptor.GlobalRequestParamsInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
@describe  RetrofitClient 单例对象
@创建时间： 2022/3/7 14:32
 */
object ServiceCreator {
    private const val DEFAULT = 0
    private const val RXJAVA = 1
    private const val LIVEDATA = 2

    const val HTTP_CONNECT_TIME_OUT = 10
    const val HTTP_READ_TIME_OUT = 15


    /**
     * RetrofitClient
     * */
    private val mOkHttpClient: OkHttpClient
        get() {

            val sslParams = HttpsUtils.getSslSocketFactory(null, null, null)

            val builder = OkHttpClient.Builder()
//                .dns(AliOKHttpDns.getInstance(Utils.getApp().applicationContext))
                .retryOnConnectionFailure(true)
                .connectTimeout(HTTP_CONNECT_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(HTTP_READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .addInterceptor(GlobalRequestParamsInterceptor())
                .sslSocketFactory(getSSL(),sslParams.trustManager)
                .hostnameVerifier { _, _ -> true }
                // 请求过滤器
//                .addInterceptor(logInterceptor)
                //设置缓存配置,缓存最大10M,设置了缓存之后可缓存请求的数据到data/data/包名/cache/net_cache目录中
//                .cache(Cache(File(BenHuApp.get().cacheDir, "net_cache"), 10 * 1024 * 1024))
                //添加缓存拦截器 可传入缓存天数
//                .addInterceptor(CacheInterceptor(3))

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    else HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                )
            }
//            build.addInterceptor(LogInterceptor())

            return builder.build()
        }

    private fun getSSL(): SSLSocketFactory {

        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<X509TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>, authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        // Install the all-trusting trust manager

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory
        return sslSocketFactory
    }

    private val mServiceMap: HashMap<String, Any> = hashMapOf()


    /**
     * Default
     * */
    private var retrofitClientDefault =
        Retrofit.Builder().baseUrl(baseCityUrl).client(mOkHttpClient)
            .addConverterFactory(FastJsonConverterFactory.create()).build()

    /**
     * RxJava
     * */
    private var retrofitClientWithRxJava =
        Retrofit.Builder().baseUrl(baseCityUrl).client(mOkHttpClient)
            .addConverterFactory(FastJsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()).build()


    /**
     * LiveData
     * */
    private var retrofitClientWithLiveData =
        Retrofit.Builder().baseUrl(baseCityUrl).client(mOkHttpClient)
            .addConverterFactory(FastJsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory()).build()

    inline fun <reified T> withLiveData(): T = createWithLiveDataApi(T::class.java)
    inline fun <reified T> withRxJava(): T = createWithRxJavaApi(T::class.java)
    inline fun <reified T> withDefault(): T = createWithDefaultApi(T::class.java)


    @JvmStatic
    fun <T> createWithDefaultApi(serviceClass: Class<T>): T = getService(serviceClass, DEFAULT)

    @JvmStatic
    fun <T> createWithRxJavaApi(serviceClass: Class<T>): T = getService(serviceClass, RXJAVA)

    @JvmStatic
    fun <T> createWithLiveDataApi(serviceClass: Class<T>): T = getService(serviceClass, LIVEDATA)


    private fun <T> getService(serviceClass: Class<T>, type: Int): T {
        val retroClient = retrofitClientDefault
        val name = serviceClass.name + retroClient.javaClass.canonicalName

        return if (mServiceMap.containsKey(name)) {
            mServiceMap[name] as T
        } else {
            val server = retroClient.create(serviceClass)
            server?.let {
                mServiceMap.put(serviceClass.name, it)
            }
            server
        }
    }


}