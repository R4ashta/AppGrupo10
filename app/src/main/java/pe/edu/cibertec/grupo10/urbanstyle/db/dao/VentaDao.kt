package pe.edu.cibertec.grupo10.urbanstyle.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.VentaEntity

@Dao
interface VentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(venta: VentaEntity)

    @Query("SELECT * FROM venta ORDER BY id DESC")
    fun obtenerTodas(): LiveData<List<VentaEntity>>

    @Query("SELECT * FROM venta ORDER BY id DESC")
    fun obtenerTodasSync(): List<VentaEntity>

    @Query("SELECT COUNT(*) FROM venta WHERE sincronizado = 0")
    fun obtenerCantidadPendientes(): LiveData<Int>

    @Query("UPDATE venta SET sincronizado = 1 WHERE sincronizado = 0")
    fun marcarComoSincronizadoAll()
}