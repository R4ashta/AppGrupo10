package pe.edu.cibertec.appgrupo10

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pe.edu.cibertec.appgrupo10.databinding.ActivityPregunta2Binding
import java.util.Locale

class Pregunta2Activity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityPregunta2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPregunta2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcular.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnCalcular) {
            calcularDemurrage()
        }
    }

    private fun calcularDemurrage() {

        val textoDias = binding.etDias.text.toString().trim()

        if (textoDias.isEmpty()) {
            binding.etDias.error = "Ingrese los días transcurridos"
            return
        }

        val dias = textoDias.toInt()

        if (dias <= 7) {

            binding.tvResultado.text =
                "Contenedor retornado dentro de los días libres."

        } else {

            val diasMora = dias - 7
            val monto = 200.00 + (75.00 * diasMora)

            val montoFormateado = String.format(
                Locale.US,
                "%.2f",
                monto
            )

            binding.tvResultado.text =
                "Días totales transcurridos: $dias\n" +
                        "Días de mora: $diasMora\n" +
                        "Monto de demurrage liquidado: S/ $montoFormateado"
        }
    }
}