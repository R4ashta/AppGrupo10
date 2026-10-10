package pe.edu.cibertec.grupo10.urbanstyle.retrofit.response

import com.google.gson.annotations.SerializedName

data class VentaSync(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("codigoTicket") val codigoTicket: String = "",
    @SerializedName("dniCliente") val dniCliente: String,
    @SerializedName("nombreCliente") val nombreCliente: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("productoNombre") val productoNombre: String,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("total") val total: Double,
    @SerializedName("montoAdelanto") val montoAdelanto: Double = 0.0,
    @SerializedName("observaciones") val observaciones: String = "",
    @SerializedName("tipo") val tipo: String = "VENTA",
    @SerializedName("sincronizado") val sincronizado: Int = 0,
    @SerializedName("fecha") val fecha: String = ""
)