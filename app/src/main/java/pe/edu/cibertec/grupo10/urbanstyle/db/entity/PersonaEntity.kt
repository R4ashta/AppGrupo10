package pe.edu.cibertec.grupo10.urbanstyle.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persona")
data class PersonaEntity(
    @PrimaryKey
    val id: String,
    val nombres: String,
    val apellidos: String,
    val email: String,
    val celular: String
)