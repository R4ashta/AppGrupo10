package pe.edu.cibertec.grupo10.urbanstyle.retrofit.response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClienteRetrofit {
    private const val BASE_URL = "https://api.urbanstyle.pe/api/v1/"

    val getInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}