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
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pe.edu.cibertec.grupo10.urbanstyle.adapter.ProductoAdapter
import pe.edu.cibertec.grupo10.urbanstyle.databinding.DialogIngresoStockBinding
import pe.edu.cibertec.grupo10.urbanstyle.databinding.FragmentCatalogoBinding
import pe.edu.cibertec.grupo10.urbanstyle.db.UrbanStyleRoomDatabase
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.ProductoEntity

class CatalogoFragment : Fragment() {

    private var _binding: FragmentCatalogoBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductoAdapter
    private var listaProductosActual: List<ProductoEntity> = emptyList()

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

        adapter = ProductoAdapter(emptyList()) { producto ->
            Toast.makeText(
                requireContext(),
                "Seleccionado: ${producto.nombre} - Stock: ${producto.stock} ud.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.rvProductos.adapter = adapter

        binding.spOrdenCatalogo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                actualizarCatalogo()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        /* Observar productos */
        val db = UrbanStyleRoomDatabase.getDatabase(requireContext().applicationContext)
        db.productoDao().obtenerTodos().observe(viewLifecycleOwner, Observer { productos ->
            listaProductosActual = productos ?: emptyList()
            actualizarCatalogo()
        })

        /* Observar stock */
        db.productoDao().obtenerTotalStock().observe(viewLifecycleOwner, Observer { total ->
            val stockTotal = total ?: 0
            binding.tvStockActual.text = getString(R.string.text_stock_actual, stockTotal)
        })

        binding.etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                actualizarCatalogo()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnIngresarStock.setOnClickListener {
            mostrarDialogoIngresoStock()
        }
    }

    private fun actualizarCatalogo() {
        val filtro = binding.etBuscarProducto.text?.toString()?.trim().orEmpty()
        val productos = listaProductosActual.filter {
            filtro.isEmpty() || it.nombre.contains(filtro, ignoreCase = true) ||
                it.color.contains(filtro, ignoreCase = true) ||
                it.talla.contains(filtro, ignoreCase = true)
        }
        val productosOrdenados = when (binding.spOrdenCatalogo.selectedItemPosition) {
            1 -> productos.sortedWith(compareBy<ProductoEntity> { it.precio }.thenBy { it.nombre.lowercase(java.util.Locale.ROOT) })
            2 -> productos.sortedWith(compareByDescending<ProductoEntity> { it.precio }.thenBy { it.nombre.lowercase(java.util.Locale.ROOT) })
            else -> productos.sortedBy { it.nombre.lowercase(java.util.Locale.ROOT) }
        }
        adapter.actualizarLista(productosOrdenados)
        binding.rvProductos.visibility = if (productos.isEmpty()) View.GONE else View.VISIBLE
        binding.tvCatalogoVacio.visibility = if (productos.isEmpty()) View.VISIBLE else View.GONE
        binding.tvCatalogoVacio.setText(
            if (filtro.isEmpty()) R.string.catalogo_vacio else R.string.catalogo_sin_resultados
        )
    }

    private fun mostrarDialogoIngresoStock() {
        val dialogBinding = DialogIngresoStockBinding.inflate(layoutInflater)
        val db = UrbanStyleRoomDatabase.getDatabase(requireContext().applicationContext)

        Thread {
            val productos = db.productoDao().obtenerTodosSync()

            activity?.runOnUiThread {
                /* Mostrar datos básicos */
                val itemsPrendas = productos.map { "${it.nombre} (${it.color} - Talla ${it.talla})" }
                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    itemsPrendas
                )
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dialogBinding.spProductosIngreso.adapter = spinnerAdapter

                var productoSeleccionado: ProductoEntity? = if (productos.isNotEmpty()) productos[0] else null

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
                        Toast.makeText(requireContext(), "Seleccionar una prenda", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (cantidad <= 0) {
                        dialogBinding.etCantidadIngreso.error = "Ingrese una cantidad válida mayor a 0"
                        return@setOnClickListener
                    }

                    /* Aumentar stock */
                    Thread {
                        db.productoDao().aumentarStock(prod.nombre, cantidad)
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "¡Ingreso registrado! +$cantidad ud. a ${prod.nombre}",
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()
                        }
                    }.start()
                }

                dialog.show()
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}