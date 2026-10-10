package pe.edu.cibertec.grupo10.urbanstyle.retrofit.response

import com.google.gson.annotations.SerializedName

data class Producto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("color") val color: String,
    @SerializedName("talla") val talla: String,
    @SerializedName("precio") val precio: Double,
    @SerializedName("stock") val stock: Int,
    @SerializedName("imagenUrl") val imagenUrl: String? = null
)