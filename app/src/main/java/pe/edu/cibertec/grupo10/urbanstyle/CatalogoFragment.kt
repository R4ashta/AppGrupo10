package pe.edu.cibertec.grupo10.urbanstyle

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pe.edu.cibertec.grupo10.urbanstyle.adapter.ProductoAdapter
import pe.edu.cibertec.grupo10.urbanstyle.database.UrbanStyleDbHelper
import pe.edu.cibertec.grupo10.urbanstyle.databinding.DialogIngresoStockBinding
import pe.edu.cibertec.grupo10.urbanstyle.databinding.FragmentCatalogoBinding
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.Producto

class CatalogoFragment : Fragment() {

    private var _binding: FragmentCatalogoBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: UrbanStyleDbHelper
    private lateinit var adapter: ProductoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = UrbanStyleDbHelper(requireContext())
        val listaInicial = dbHelper.obtenerTodosProductos()

        adapter = ProductoAdapter(listaInicial) { producto ->
            Toast.makeText(
                requireContext(),
                "Seleccionado: ${producto.nombre} - Stock: ${producto.stock} ud.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.rvProductos.adapter = adapter
        actualizarResumenStock()

        binding.etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtro = s?.toString()?.trim() ?: ""
                val listaFiltrada = if (filtro.isEmpty()) {
                    dbHelper.obtenerTodosProductos()
                } else {
                    dbHelper.buscarProductos(filtro)
                }
                adapter.actualizarLista(listaFiltrada)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnIngresarStock.setOnClickListener {
            mostrarDialogoIngresoStock()
        }
    }

    private fun mostrarDialogoIngresoStock() {
        val dialogBinding = DialogIngresoStockBinding.inflate(layoutInflater)
        val productos = dbHelper.obtenerTodosProductos()

        /* Mostrar solo Nombre, Color y Talla*/

        val itemsPrendas = productos.map { "${it.nombre} (${it.color} - Talla ${it.talla})" }
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            itemsPrendas
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spProductosIngreso.adapter = spinnerAdapter

        var productoSeleccionado: Producto? = if (productos.isNotEmpty()) productos[0] else null

        dialogBinding.spProductosIngreso.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in productos.indices) {
                    productoSeleccionado = productos[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val dialog: AlertDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancelarIngreso.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnGuardarIngreso.setOnClickListener {
            val prod = productoSeleccionado
            val cantStr = dialogBinding.etCantidadIngreso.text.toString().trim()
            val cantidad = cantStr.toIntOrNull() ?: 0

            if (prod == null) {
                Toast.makeText(requireContext(), "Seleccione una prenda", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cantidad <= 0) {
                dialogBinding.etCantidadIngreso.error = "Ingrese una cantidad válida mayor a 0"
                return@setOnClickListener
            }

            /* Aumentar stock en SQLite */
            dbHelper.aumentarStock(prod.nombre, cantidad)

            Toast.makeText(
                requireContext(),
                "¡Ingreso registrado! +$cantidad ud. a ${prod.nombre}",
                Toast.LENGTH_LONG
            ).show()

            dialog.dismiss()

            /*Refrescar lista y stock*/
            val nuevaLista = dbHelper.obtenerTodosProductos()
            adapter.actualizarLista(nuevaLista)
            actualizarResumenStock()
        }

        dialog.show()
    }

    private fun actualizarResumenStock() {
        val totalStock = dbHelper.obtenerTotalStock()
        binding.tvStockActual.text = getString(R.string.text_stock_actual, totalStock)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}