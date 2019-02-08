package io.chipotie.grindemo.net

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.chipotie.grindemo.util.PermissionUtil
import io.chipotie.grindemo.util.SingletonHolder
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class GrinApiSingleton private constructor(val context: Context){

    var grinApiInterface : GrinApiInterface

    init {
        val builder = Retrofit.Builder()
            .baseUrl("https://grin-bluetooth-api.herokuapp.com")
            .client(OkHttpClient())

        grinApiInterface = builder.build().create(GrinApiInterface::class.java)
    }

    companion object : SingletonHolder<GrinApiSingleton, Context>(::GrinApiSingleton)
}