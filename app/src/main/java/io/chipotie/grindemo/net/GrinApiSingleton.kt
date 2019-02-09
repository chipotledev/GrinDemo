package io.chipotie.grindemo.net

import android.content.Context
import io.chipotie.grindemo.BuildConfig
import io.chipotie.grindemo.util.SingletonHolder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GrinApiSingleton private constructor(val context: Context){

    var grinApiInterface : GrinApiInterface

    init {

        //Interceptor
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        //OkHttp Client
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addInterceptor(interceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)

        val builder = Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpBuilder.build())

        grinApiInterface = builder.build().create(GrinApiInterface::class.java)
    }

    companion object : SingletonHolder<GrinApiSingleton, Context>(::GrinApiSingleton)
}