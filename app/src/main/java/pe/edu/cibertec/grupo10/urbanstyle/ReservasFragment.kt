package pe.edu.cibertec.grupo10.urbanstyle

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import pe.edu.cibertec.grupo10.urbanstyle.databinding.FragmentReservasBinding
import pe.edu.cibertec.grupo10.urbanstyle.db.UrbanStyleRoomDatabase
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.ProductoEntity
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.VentaEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReservasFragment : Fragment() {

    private var _binding: FragmentReservasBinding? = null
    private val binding get() = _binding!!

    private var listaProductos: List<ProductoEntity> = emptyList()
    private var productoSeleccionado: ProductoEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = UrbanStyleRoomDatabase.getDatabase(requireContext().applicationContext)

        db.productoDao().obtenerTodos().observe(viewLifecycleOwner, Observer { productos ->
            listaProductos = productos ?: emptyList()
            
            /* Opción por defecto */
            val nombresProductos = mutableListOf("Selecciona una prenda...")
            nombresProductos.addAll(listaProductos.map { "${it.nombre} (${it.color} - Talla ${it.talla}) - S/ ${it.precio}" })
            
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_spinner_item,
                nombresProductos
            )
            spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spProductosReserva.adapter = spinnerAdapter
        })

        binding.spProductosReserva.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0 && position - 1 in listaProductos.indices) {
                    productoSeleccionado = listaProductos[position - 1]
                    calcularTotal()
                } else {
                    productoSeleccionado = null
                    binding.tvTotalReserva.text = "Total Reserva: S/ 0.00"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.etCantidadReserva.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calcularTotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnRegistrarReserva.setOnClickListener {
            registrarReservaOffline()
        }
    }

    private fun calcularTotal() {
        val prod = productoSeleccionado ?: return
        val cantStr = binding.etCantidadReserva.text.toString().trim()
        val cantidad = cantStr.toIntOrNull() ?: 1
        val total = prod.precio * cantidad
        binding.tvTotalReserva.text = String.format(Locale.US, "Total Reserva: S/ %.2f", total)
    }

    private fun registrarReservaOffline() {
        val dni = binding.etDniReserva.text.toString().trim()
        val nombre = binding.etNombreReserva.text.toString().trim()
        val direccion = binding.etDireccionReserva.text.toString().trim()
        val cantStr = binding.etCantidadReserva.text.toString().trim()
        val cantidad = cantStr.toIntOrNull() ?: 0
        val adelantoStr = binding.etMontoAdelanto.text.toString().trim()
        val adelanto = adelantoStr.toDoubleOrNull() ?: 0.0
        val observaciones = binding.etObservaciones.text.toString().trim()

        if (dni.length != 8) {
            binding.etDniReserva.error = "DNI debe tener 8 dígitos"
            return
        }
        if (nombre.isEmpty()) {
            binding.etNombreReserva.error = "Ingrese el nombre del cliente"
            return
        }
        if (direccion.isEmpty()) {
            binding.etDireccionReserva.error = "Ingrese la dirección"
            return
        }
        if (cantidad <= 0) {
            binding.etCantidadReserva.error = "Cantidad inválida"
            return
        }

        val prod = productoSeleccionado
        if (prod == null) {
            Toast.makeText(requireContext(), "Seleccione una prenda", Toast.LENGTH_SHORT).show()
            return
        }

        val total = prod.precio * cantidad
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val fechaTicket = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())
        val ticket = "R-$fechaTicket-${(1000..9999).random()}"

        val reserva = VentaEntity(
            codigoTicket = ticket,
            dniCliente = dni,
            nombreCliente = nombre,
            direccion = direccion,
            productoNombre = prod.nombre,
            cantidad = cantidad,
            total = total,
            montoAdelanto = adelanto,
            observaciones = observaciones,
            tipo = "RESERVA",
            fecha = fecha
        )

        val db = UrbanStyleRoomDatabase.getDatabase(requireContext().applicationContext)
        Thread {
            db.ventaDao().insertar(reserva)
            db.productoDao().descontarStock(prod.nombre, cantidad)

            activity?.runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "¡Reserva offline registrada correctamente en Room!",
                    Toast.LENGTH_LONG
                ).show()

                /* Limpiar datos */
                binding.etDniReserva.text?.clear()
                binding.etNombreReserva.text?.clear()
                binding.etDireccionReserva.text?.clear()
                binding.spProductosReserva.setSelection(0)
                binding.etMontoAdelanto.text?.clear()
                binding.etObservaciones.text?.clear()
                binding.etCantidadReserva.setText("1")
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}