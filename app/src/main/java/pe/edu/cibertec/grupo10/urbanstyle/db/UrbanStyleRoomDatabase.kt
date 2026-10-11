package pe.edu.cibertec.grupo10.urbanstyle.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import pe.edu.cibertec.grupo10.urbanstyle.db.dao.PersonaDao
import pe.edu.cibertec.grupo10.urbanstyle.db.dao.ProductoDao
import pe.edu.cibertec.grupo10.urbanstyle.db.dao.VentaDao
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.PersonaEntity
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.ProductoEntity
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.VentaEntity

@Database(entities = [ProductoEntity::class, VentaEntity::class, PersonaEntity::class], version = 1)
abstract class UrbanStyleRoomDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao
    abstract fun personaDao(): PersonaDao

    companion object {
        @Volatile
        private var INSTANCE: UrbanStyleRoomDatabase? = null

        fun getDatabase(context: Context): UrbanStyleRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UrbanStyleRoomDatabase::class.java,
                    "urbanstyledb"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            /* Inventario inicial */
                            Thread {
                                val database = getDatabase(context)
                                database.productoDao().insertarLista(
                                    listOf(
                                        ProductoEntity(0, "Jogger Urbano Oversize", "Negro Mate", "L", 89.90, 25),
                                        ProductoEntity(0, "Polo Heavyweight UrbanStyle", "Blanco Humo", "M", 59.90, 40),
                                        ProductoEntity(0, "Polera Hoodie Cargo", "Gris Plomo", "XL", 129.90, 15),
                                        ProductoEntity(0, "Casaca Denim Streetwear", "Azul Faded", "L", 189.90, 10),
                                        ProductoEntity(0, "Pantalón Cargo Tactical", "Verde Olivo", "32", 119.90, 20),
                                        ProductoEntity(0, "Polo Boxy Fit Graphic", "Beige Arena", "S", 64.90, 30)
                                    )
                                )
                            }.start()
                        }
                    })
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}