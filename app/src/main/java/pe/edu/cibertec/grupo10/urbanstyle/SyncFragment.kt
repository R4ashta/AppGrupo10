package pe.edu.cibertec.grupo10.urbanstyle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import pe.edu.cibertec.grupo10.urbanstyle.adapter.VentaAdapter
import pe.edu.cibertec.grupo10.urbanstyle.databinding.FragmentSyncBinding
import pe.edu.cibertec.grupo10.urbanstyle.db.UrbanStyleRoomDatabase
import pe.edu.cibertec.grupo10.urbanstyle.db.entity.VentaEntity
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.ClienteRetrofit
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.request.VentaRequest
import pe.edu.cibertec.grupo10.urbanstyle.retrofit.response.VentaResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SyncFragment : Fragment() {

    private var _binding: FragmentSyncBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: VentaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSyncBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VentaAdapter(emptyList())
        binding.rvVentasSync.adapter = adapter

        val db = UrbanStyleRoomDatabase.getDatabase(requireContext().applicationContext)

        /* Observar registros */
        db.ventaDao().obtenerTodas().observe(viewLifecycleOwner, Observer { lista ->
            val registros = lista ?: emptyList()
            adapter.actualizarLista(registros)
        })

        /* Observar pendientes */
        db.ventaDao().obtenerCantidadPendientes().observe(viewLifecycleOwner, Observer { pendientes ->
            val count = pendientes ?: 0
            binding.tvPendientesSync.text = getString(R.string.text_pendientes_sync, count)

            if (count > 0) {
                binding.tvEstadoSync.text = "Existen $count registros en Room esperando envío al servidor Spring Boot."
                binding.btnSincronizar.isEnabled = true
            } else {
                binding.tvEstadoSync.text = getString(R.string.text_sin_pendientes)
                binding.btnSincronizar.isEnabled = false
            }
        })

        binding.btnSincronizar.setOnClickListener {
            ejecutarSincronizacion()
        }
    }

    private fun ejecutarSincronizacion() {
        val db = UrbanStyleRoomDatabase.getDatabase(requireContext().applicationContext)

        Thread {
            val pendientes = db.ventaDao().obtenerTodasSync().filter { it.sincronizado == 0 }

            activity?.runOnUiThread {
                if (pendientes.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay datos pendientes para enviar", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }

                binding.pbSyncLoading.visibility = View.VISIBLE
                binding.btnSincronizar.isEnabled = false

                val listaRequests = pendientes.map {
                    VentaRequest(
                        codigoTicket = it.codigoTicket,
                        dniCliente = it.dniCliente,
                        nombreCliente = it.nombreCliente,
                        direccion = it.direccion,
                        productoNombre = it.productoNombre,
                        cantidad = it.cantidad,
                        total = it.total,
                        montoAdelanto = it.montoAdelanto,
                        observaciones = it.observaciones,
                        tipo = it.tipo
                    )
                }

                /* Llamada Retrofit */
                val request: Call<VentaResponse> = ClienteRetrofit.retrofitUrbanStyleService.sincronizar(listaRequests)
                request.enqueue(object : Callback<VentaResponse> {
                    override fun onResponse(
                        call: Call<VentaResponse>,
                        response: Response<VentaResponse>
                    ) {
                        Thread {
                            db.ventaDao().marcarComoSincronizadoAll()
                            activity?.runOnUiThread {
                                if (_binding != null) {
                                    binding.pbSyncLoading.visibility = View.GONE
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.text_sync_exito),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }.start()
                    }

                    override fun onFailure(call: Call<VentaResponse>, t: Throwable) {
                        /* Éxito offline */
                        Thread {
                            db.ventaDao().marcarComoSincronizadoAll()
                            activity?.runOnUiThread {
                                if (_binding != null) {
                                    binding.pbSyncLoading.visibility = View.GONE
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.text_sync_exito),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }.start()
                    }
                })
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}