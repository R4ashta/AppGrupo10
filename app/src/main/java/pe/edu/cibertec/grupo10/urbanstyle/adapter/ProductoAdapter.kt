package pe.edu.cibertec.grupo10.urbanstyle.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.edu.cibertec.grupo10.urbanstyle.databinding.ItemProductoBinding
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.Producto
import java.util.Locale

class ProductoAdapter(
    private var listaProductos: List<Producto>,
    private val onItemClick: ((Producto) -> Unit)? = null
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(val binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]
        with(holder.binding) {
            tvNombreProducto.text = producto.nombre
            tvColorProducto.text = "Color: ${producto.color}"
            tvTallaProducto.text = "Talla: ${producto.talla}"
            tvStockProducto.text = "Stock: ${producto.stock} ud."
            tvPrecioProducto.text = String.format(Locale.US, "S/ %.2f", producto.precio)

            root.setOnClickListener {
                onItemClick?.invoke(producto)
            }
        }
    }

    override fun getItemCount(): Int = listaProductos.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        listaProductos = nuevaLista
        notifyDataSetChanged()
    }
}