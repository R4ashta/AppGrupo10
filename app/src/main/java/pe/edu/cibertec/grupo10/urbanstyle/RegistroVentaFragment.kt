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
import pe.edu.cibertec.grupo10.urbanstyle.database.UrbanStyleDbHelper
import pe.edu.cibertec.grupo10.urbanstyle.databinding.FragmentRegistroVentaBinding
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.Producto
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.VentaSync
import java.util.Locale

class RegistroVentaFragment : Fragment() {

    private var _binding: FragmentRegistroVentaBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: UrbanStyleDbHelper
    private var listaProductos: List<Producto> = emptyList()
    private var productoSeleccionado: Producto? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroVentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = UrbanStyleDbHelper(requireContext())
        listaProductos = dbHelper.obtenerTodosProductos()

        val nombresProductos = listaProductos.map { "${it.nombre} (${it.color} - Talla ${it.talla}) - S/ ${it.precio}" }
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            nombresProductos
        )
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spProductos.adapter = spinnerAdapter

        binding.spProductos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in listaProductos.indices) {
                    productoSeleccionado = listaProductos[position]
                    calcularTotal()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.etCantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calcularTotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnRegistrarVenta.setOnClickListener {
            registrarVentaOffline()
        }
    }

    private fun calcularTotal() {
        val prod = productoSeleccionado ?: return
        val cantStr = binding.etCantidad.text.toString().trim()
        val cantidad = cantStr.toIntOrNull() ?: 1
        val total = prod.precio * cantidad
        binding.tvTotalPagar.text = String.format(Locale.US, "Total a Pagar: S/ %.2f", total)
    }

    private fun registrarVentaOffline() {
        val dni = binding.etDniCliente.text.toString().trim()
        val nombre = binding.etNombreCliente.text.toString().trim()
        val direccion = binding.etDireccionCliente.text.toString().trim()
        val cantStr = binding.etCantidad.text.toString().trim()
        val cantidad = cantStr.toIntOrNull() ?: 0

        if (dni.length != 8) {
            binding.etDniCliente.error = "DNI debe tener 8 dígitos"
            return
        }
        if (nombre.isEmpty()) {
            binding.etNombreCliente.error = "Ingrese el nombre del cliente"
            return
        }
        if (direccion.isEmpty()) {
            binding.etDireccionCliente.error = "Ingrese la dirección"
            return
        }
        if (cantidad <= 0) {
            binding.etCantidad.error = "Cantidad inválida"
            return
        }

        val prod = productoSeleccionado
        if (prod == null) {
            Toast.makeText(requireContext(), "Seleccione una prenda", Toast.LENGTH_SHORT).show()
            return
        }

        if (cantidad > prod.stock) {
            Toast.makeText(
                requireContext(),
                "Stock insuficiente (Disponible: ${prod.stock})",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val total = prod.precio * cantidad
        val venta = VentaSync(
            dniCliente = dni,
            nombreCliente = nombre,
            direccion = direccion,
            productoNombre = prod.nombre,
            cantidad = cantidad,
            total = total,
            tipo = "VENTA"
        )

        val exito = dbHelper.registrarVentaOReserva(venta)
        if (exito) {
            Toast.makeText(
                requireContext(),
                "¡Venta offline registrada correctamente en SQLite!",
                Toast.LENGTH_LONG
            ).show()

            /* Limpiar formulario */
            binding.etDniCliente.text?.clear()
            binding.etNombreCliente.text?.clear()
            binding.etDireccionCliente.text?.clear()
            binding.etCantidad.setText("1")

            /* Actualizar stock */
            listaProductos = dbHelper.obtenerTodosProductos()
        } else {
            Toast.makeText(requireContext(), "Error al guardar la venta en SQLite", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}