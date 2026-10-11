package pe.edu.cibertec.grupo10.urbanstyle.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "venta")
data class VentaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val codigoTicket: String,
    val dniCliente: String,
    val nombreCliente: String,
    val direccion: String,
    val productoNombre: String,
    val cantidad: Int,
    val total: Double,
    val montoAdelanto: Double = 0.0,
    val observaciones: String = "",
    val tipo: String = "VENTA",
    val sincronizado: Int = 0,
    val fecha: String
)