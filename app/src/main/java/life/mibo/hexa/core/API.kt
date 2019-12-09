package life.mibo.hexa.core

import life.mibo.hexa.core.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit


class API private constructor() {


    var okhttp: OkHttpClient? = null
    val request: API by lazy { API() }

    companion object {
        //val baseUrl = "https://os.mibo.world/api/v1/"
        val baseUrl = "https://test3.mibo.world/api/v1/"
    }

    fun getApi(): ApiService {
        return Retrofit.Builder().baseUrl(baseUrl)
            .client(okhttp!!).addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }


    interface ApiService {

    }
}