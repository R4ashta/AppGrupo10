package pe.edu.cibertec.grupo10.urbanstyle.retrofit.request

data class VentaRequest(
    var codigoTicket: String,
    var dniCliente: String,
    var nombreCliente: String,
    var direccion: String,
    var productoNombre: String,
    var cantidad: Int,
    var total: Double,
    var montoAdelanto: Double,
    var observaciones: String,
    var tipo: String
)