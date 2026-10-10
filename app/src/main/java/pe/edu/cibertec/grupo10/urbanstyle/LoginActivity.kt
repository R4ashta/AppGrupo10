package pe.edu.cibertec.grupo10.urbanstyle

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pe.edu.cibertec.grupo10.urbanstyle.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* Ocultar mensaje de error al escribir */
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvErrorLogin.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etUsuario.addTextChangedListener(textWatcher)
        binding.etClave.addTextChangedListener(textWatcher)

        /* Botón Iniciar Sesión */
        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val clave = binding.etClave.text.toString().trim()

            /* Verificación de credenciales (Admin o Vendedor activo) */
            val credencialValida = (usuario.equals("admin", ignoreCase = true) ||
                    usuario.equals("vendedor", ignoreCase = true) ||
                    usuario == "72819203") && (clave == "1234" || clave == "admin")

            if (!credencialValida) {
                /* Mostrar alerta / mensaje de error exacto en pantalla */
                binding.tvErrorLogin.visibility = View.VISIBLE
                return@setOnClickListener
            }

            /* Credencial correcta: Ocultar error e ingresar */
            binding.tvErrorLogin.visibility = View.GONE
            Toast.makeText(this, "Bienvenido a UrbanStyle, $usuario", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        /* Enlace ¿Olvidaste tu contraseña? */
        binding.tvOlvidasteClave.setOnClickListener {
            mostrarDialogoRecuperacion()
        }

        /* Enlace Contactar a Soporte */
        binding.tvSoporte.setOnClickListener {
            mostrarDialogoRecuperacion()
        }
    }

    /* Mostrar Modal de Recuperación de Acceso */
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