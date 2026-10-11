package pe.edu.cibertec.grupo10.urbanstyle.retrofit

import okhttp3.OkHttpClient
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.api.UrbanStyleService
import pe.edu.cibertec.grupo10.urbanstyle.util.Constantes
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ClienteRetrofit {

    private var okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private fun buildRetrofit() = Retrofit.Builder()
        .baseUrl(Constantes.URL_API_BASE)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitUrbanStyleService: UrbanStyleService by lazy {
        buildRetrofit().create(UrbanStyleService::class.java)
    }
}