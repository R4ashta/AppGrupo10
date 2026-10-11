package pe.edu.cibertec.grupo10.urbanstyle

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pe.edu.cibertec.grupo10.urbanstyle.databinding.ActivityLoginBinding
import pe.edu.cibertec.grupo10.urbanstyle.db.UrbanStyleRoomDatabase
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.PersonaEntity

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener(this)
        binding.tvOlvidasteClave.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnIngresar -> login()

        }
    }

    private fun login() {
        val usuario = binding.etUsuario.text.toString().trim()
        val clave = binding.etClave.text.toString().trim()

        /* Verificar credenciales */
        val credencialValida = (usuario.equals("admin", ignoreCase = true) ||
                usuario.equals("vendedor", ignoreCase = true) ||
                usuario == "72819203") && (clave == "1234" || clave == "admin")

        if (!credencialValida) {
            /* Mostrar error */
            mostrarAlertaError()
            return
        }

        /* Credencial correcta */
        val persona = PersonaEntity(
            id = usuario,
            nombres = "Vendedor",
            apellidos = "UrbanStyle",
            email = "vendedor@urbanstyle.pe",
            celular = "987654321"
        )

        Thread {
            val db = UrbanStyleRoomDatabase.getDatabase(applicationContext)
            db.personaDao().eliminarTodo()
            db.personaDao().insertar(persona)
            runOnUiThread {
                Toast.makeText(this, "Bienvenido a UrbanStyle, $usuario", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }

    /* Mostrar error */
    private fun mostrarAlertaError() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.text_error_login)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /* Mostrar recuperación */
    private fun mostrarDialogoRecuperacion() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.title_recuperacion_acceso)
            .setMessage(R.string.msg_recuperacion_acceso)
            .setPositiveButton(R.string.btn_entendido) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}