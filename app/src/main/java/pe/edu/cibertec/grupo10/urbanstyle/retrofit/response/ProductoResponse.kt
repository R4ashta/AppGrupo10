package pe.edu.cibertec.grupo10.urbanstyle.retrofit.response

data class ProductoResponse(
    var id: Int,
    var nombre: String,
    var color: String,
    var talla: String,
    var precio: Double,
    var stock: Int
)