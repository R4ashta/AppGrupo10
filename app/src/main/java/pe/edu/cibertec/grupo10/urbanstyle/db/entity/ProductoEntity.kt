package pe.edu.cibertec.grupo10.urbanstyle.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val color: String,
    val talla: String,
    val precio: Double,
    val stock: Int
)