package pe.edu.cibertec.grupo10.urbanstyle.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.Producto
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.VentaSync
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UrbanStyleDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "urbanstyle.db"
        private const val DATABASE_VERSION = 2

        /* Tabla Productos */
        const val TABLE_PRODUCTO = "productos"
        const val COL_PROD_ID = "id"
        const val COL_PROD_NOMBRE = "nombre"
        const val COL_PROD_COLOR = "color"
        const val COL_PROD_TALLA = "talla"
        const val COL_PROD_PRECIO = "precio"
        const val COL_PROD_STOCK = "stock"

        /* Tabla Ventas y Reservas */
        const val TABLE_VENTA = "ventas"
        const val COL_VENTA_ID = "id"
        const val COL_VENTA_TICKET = "codigo_ticket"
        const val COL_VENTA_DNI = "dni_cliente"
        const val COL_VENTA_NOMBRE = "nombre_cliente"
        const val COL_VENTA_DIRECCION = "direccion"
        const val COL_VENTA_PRODUCTO = "producto_nombre"
        const val COL_VENTA_CANTIDAD = "cantidad"
        const val COL_VENTA_TOTAL = "total"
        const val COL_VENTA_ADELANTO = "monto_adelanto"
        const val COL_VENTA_OBSERVACIONES = "observaciones"
        const val COL_VENTA_TIPO = "tipo"
        const val COL_VENTA_SYNC = "sincronizado"
        const val COL_VENTA_FECHA = "fecha"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createProductoTable = """
            CREATE TABLE $TABLE_PRODUCTO (
                $COL_PROD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PROD_NOMBRE TEXT NOT NULL,
                $COL_PROD_COLOR TEXT NOT NULL,
                $COL_PROD_TALLA TEXT NOT NULL,
                $COL_PROD_PRECIO REAL NOT NULL,
                $COL_PROD_STOCK INTEGER NOT NULL
            )
        """.trimIndent()

        val createVentaTable = """
            CREATE TABLE $TABLE_VENTA (
                $COL_VENTA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_VENTA_TICKET TEXT UNIQUE NOT NULL,
                $COL_VENTA_DNI TEXT NOT NULL,
                $COL_VENTA_NOMBRE TEXT NOT NULL,
                $COL_VENTA_DIRECCION TEXT NOT NULL,
                $COL_VENTA_PRODUCTO TEXT NOT NULL,
                $COL_VENTA_CANTIDAD INTEGER NOT NULL,
                $COL_VENTA_TOTAL REAL NOT NULL,
                $COL_VENTA_ADELANTO REAL DEFAULT 0.0,
                $COL_VENTA_OBSERVACIONES TEXT,
                $COL_VENTA_TIPO TEXT NOT NULL,
                $COL_VENTA_SYNC INTEGER DEFAULT 0,
                $COL_VENTA_FECHA TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createProductoTable)
        db.execSQL(createVentaTable)

        insertarProductosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VENTA")
        onCreate(db)
    }

    private fun insertarProductosIniciales(db: SQLiteDatabase) {
        val listaInicial = listOf(
            Producto(0, "Jogger Urbano Oversize", "Negro Mate", "L", 89.90, 25),
            Producto(0, "Polo Heavyweight UrbanStyle", "Blanco Humo", "M", 59.90, 40),
            Producto(0, "Polera Hoodie Cargo", "Gris Plomo", "XL", 129.90, 15),
            Producto(0, "Casaca Denim Streetwear", "Azul Faded", "L", 189.90, 10),
            Producto(0, "Pantalón Cargo Tactical", "Verde Olivo", "32", 119.90, 20),
            Producto(0, "Polo Boxy Fit Graphic", "Beige Arena", "S", 64.90, 30)
        )

        for (p in listaInicial) {
            val cv = ContentValues().apply {
                put(COL_PROD_NOMBRE, p.nombre)
                put(COL_PROD_COLOR, p.color)
                put(COL_PROD_TALLA, p.talla)
                put(COL_PROD_PRECIO, p.precio)
                put(COL_PROD_STOCK, p.stock)
            }
            db.insert(TABLE_PRODUCTO, null, cv)
        }
    }

    /* CRUD Productos */
    fun obtenerTodosProductos(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PRODUCTO", null)
        if (cursor.moveToFirst()) {
            do {
                val producto = Producto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_NOMBRE)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_COLOR)),
                    talla = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_TALLA)),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_PRECIO)),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_STOCK))
                )
                lista.add(producto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun buscarProductos(filtro: String): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_PRODUCTO WHERE $COL_PROD_NOMBRE LIKE ? OR $COL_PROD_COLOR LIKE ? OR $COL_PROD_TALLA LIKE ?"
        val pattern = "%$filtro%"
        val cursor = db.rawQuery(query, arrayOf(pattern, pattern, pattern))
        if (cursor.moveToFirst()) {
            do {
                val producto = Producto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ID)),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_NOMBRE)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_COLOR)),
                    talla = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_TALLA)),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_PRECIO)),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_STOCK))
                )
                lista.add(producto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun obtenerTotalStock(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM($COL_PROD_STOCK) FROM $TABLE_PRODUCTO", null)
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    fun descontarStock(nombreProducto: String, cantidad: Int) {
        val db = writableDatabase
        val query = "UPDATE $TABLE_PRODUCTO SET $COL_PROD_STOCK = MAX(0, $COL_PROD_STOCK - ?) WHERE $COL_PROD_NOMBRE = ?"
        db.execSQL(query, arrayOf(cantidad, nombreProducto))
    }

    fun aumentarStock(nombreProducto: String, cantidad: Int) {
        val db = writableDatabase
        val query = "UPDATE $TABLE_PRODUCTO SET $COL_PROD_STOCK = $COL_PROD_STOCK + ? WHERE $COL_PROD_NOMBRE = ?"
        db.execSQL(query, arrayOf(cantidad, nombreProducto))
    }

    /* Generar Ticket */
    fun generarCodigoTicket(prefijo: String): String {
        val fecha = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())
        val aleatorio = (1000..9999).random()
        return "$prefijo-$fecha-$aleatorio"
    }

    /* CRUD Ventas / Reservas */
    fun registrarVentaOReserva(registro: VentaSync): Boolean {
        val db = writableDatabase
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val prefijo = if (registro.tipo == "RESERVA") "R" else "V"
        val ticket = if (registro.codigoTicket.isEmpty()) generarCodigoTicket(prefijo) else registro.codigoTicket

        val cv = ContentValues().apply {
            put(COL_VENTA_TICKET, ticket)
            put(COL_VENTA_DNI, registro.dniCliente)
            put(COL_VENTA_NOMBRE, registro.nombreCliente)
            put(COL_VENTA_DIRECCION, registro.direccion)
            put(COL_VENTA_PRODUCTO, registro.productoNombre)
            put(COL_VENTA_CANTIDAD, registro.cantidad)
            put(COL_VENTA_TOTAL, registro.total)
            put(COL_VENTA_ADELANTO, registro.montoAdelanto)
            put(COL_VENTA_OBSERVACIONES, registro.observaciones)
            put(COL_VENTA_TIPO, registro.tipo)
            put(COL_VENTA_SYNC, 0)
            put(COL_VENTA_FECHA, fechaActual)
        }
        val result = db.insert(TABLE_VENTA, null, cv)
        if (result != -1L) {
            descontarStock(registro.productoNombre, registro.cantidad)
            return true
        }
        return false
    }

    fun obtenerRegistrosLocales(): List<VentaSync> {
        val lista = mutableListOf<VentaSync>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_VENTA ORDER BY $COL_VENTA_ID DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val registro = VentaSync(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_VENTA_ID)),
                    codigoTicket = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENTA_TICKET)),
                    dniCliente = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENTA_DNI)),
                    nombreCliente = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENTA_NOMBRE)),
                    direccion = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENTA_DIRECCION)),
                    productoNombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENTA_PRODUCTO)),
                    cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(COL_VENTA_CANTIDAD)),
                    total = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_VENTA_TOTAL)),
                    montoAdelanto = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_VENTA_ADELANTO)),
                    observaciones = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENTA_OBSERVACIONES)) ?: "",
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENTA_TIPO)),
                    sincronizado = cursor.getInt(cursor.getColumnIndexOrThrow(COL_VENTA_SYNC)),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_VENTA_FECHA))
                )
                lista.add(registro)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun obtenerCantidadPendientesSync(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_VENTA WHERE $COL_VENTA_SYNC = 0", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun marcarComoSincronizadoAll() {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_VENTA_SYNC, 1)
        }
        db.update(TABLE_VENTA, cv, "$COL_VENTA_SYNC = 0", null)
    }
}