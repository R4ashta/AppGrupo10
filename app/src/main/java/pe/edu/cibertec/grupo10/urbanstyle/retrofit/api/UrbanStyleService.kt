package pe.edu.cibertec.grupo10.urbanstyle.retrofit.api

import pe.edu.cibertec.grupo10.urbanstyle.retrofit.request.LoginRequest
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.request.VentaRequest
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.LoginResponse
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.ProductoResponse
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.VentaResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UrbanStyleService {

    @POST("login.php")
    fun autenticacion(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("productos.php")
    fun obtenerProductos(): Call<List<ProductoResponse>>

    @POST("sincronizar.php")
    fun sincronizar(@Body ventas: List<VentaRequest>): Call<VentaResponse>
}