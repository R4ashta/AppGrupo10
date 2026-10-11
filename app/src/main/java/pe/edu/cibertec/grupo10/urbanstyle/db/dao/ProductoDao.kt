package pe.edu.cibertec.grupo10.urbanstyle.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.ProductoEntity

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(producto: ProductoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarLista(productos: List<ProductoEntity>)

    @Update
    fun actualizar(producto: ProductoEntity)

    @Query("SELECT * FROM producto")
    fun obtenerTodos(): LiveData<List<ProductoEntity>>

    @Query("SELECT * FROM producto")
    fun obtenerTodosSync(): List<ProductoEntity>

    @Query("SELECT * FROM producto WHERE nombre LIKE :filtro OR color LIKE :filtro OR talla LIKE :filtro")
    fun buscarProductos(filtro: String): List<ProductoEntity>

    @Query("SELECT SUM(stock) FROM producto")
    fun obtenerTotalStock(): LiveData<Int?>

    @Query("UPDATE producto SET stock = MAX(0, stock - :cantidad) WHERE nombre = :nombre")
    fun descontarStock(nombre: String, cantidad: Int)

    @Query("UPDATE producto SET stock = stock + :cantidad WHERE nombre = :nombre")
    fun aumentarStock(nombre: String, cantidad: Int)
}