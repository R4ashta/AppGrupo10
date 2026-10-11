package pe.edu.cibertec.grupo10.urbanstyle.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.edu.cibertec.grupo10.urbanstyle.R
import pe.edu.cibertec.grupo10.urbanstyle.databinding.ItemVentaBinding
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.VentaEntity
import java.util.Locale

class VentaAdapter(
    private var listaVentas: List<VentaEntity>
) : RecyclerView.Adapter<VentaAdapter.VentaViewHolder>() {

    class VentaViewHolder(val binding: ItemVentaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaViewHolder {
        val binding = ItemVentaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VentaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VentaViewHolder, position: Int) {
        val venta = listaVentas[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvVentaTipo.text = venta.tipo
            tvVentaTicket.text = "Ticket: ${venta.codigoTicket}"
            tvVentaCliente.text = "Cliente: ${venta.nombreCliente} (DNI: ${venta.dniCliente})"
            tvVentaDetalle.text = "${venta.productoNombre} x ${venta.cantidad}"
            tvVentaTotal.text = String.format(Locale.US, "S/ %.2f", venta.total)
            tvVentaFecha.text = "Fecha: ${venta.fecha}"

            if (venta.sincronizado == 1) {
                tvVentaEstadoSync.text = "SINCRONIZADO"
                tvVentaEstadoSync.setBackgroundColor(ContextCompat.getColor(context, R.color.urban_green))
                tvVentaEstadoSync.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                tvVentaEstadoSync.text = "PENDIENTE"
                tvVentaEstadoSync.setBackgroundColor(ContextCompat.getColor(context, R.color.urban_accent))
                tvVentaEstadoSync.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }

    override fun getItemCount(): Int = listaVentas.size

    fun actualizarLista(nuevaLista: List<VentaEntity>) {
        listaVentas = nuevaLista
        notifyDataSetChanged()
    }
}