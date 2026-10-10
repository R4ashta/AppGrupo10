package pe.edu.cibertec.grupo10.urbanstyle.retrofit.api

import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.VentaSync
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ISyncService {
    @POST("sincronizar")
    fun sincronizarRegistros(@Body registros: List<VentaSync>): Call<Map<String, Any>>
}