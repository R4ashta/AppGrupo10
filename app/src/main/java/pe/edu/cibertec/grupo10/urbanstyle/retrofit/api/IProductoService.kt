package pe.edu.cibertec.grupo10.urbanstyle.retrofit.api

import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.Producto
import retrofit2.Call
import retrofit2.http.GET

interface IProductoService {
    @GET("productos")
    fun obtenerProductos(): Call<List<Producto>>
}