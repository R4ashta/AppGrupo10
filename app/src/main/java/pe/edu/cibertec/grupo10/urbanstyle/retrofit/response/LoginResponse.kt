package pe.edu.cibertec.grupo10.urbanstyle.retrofit.response

data class LoginResponse(
    var rpta: Boolean,
    var idpersona: String,
    var nombres: String,
    var apellidos: String,
    var email: String,
    var celular: String,
    var mensaje: String
)