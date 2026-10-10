package pe.edu.cibertec.grupo10.urbanstyle

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import pe.edu.cibertec.grupo10.urbanstyle.adapter.VentaAdapter
import pe.edu.cibertec.grupo10.urbanstyle.database.UrbanStyleDbHelper
import pe.edu.cibertec.grupo10.urbanstyle.databinding.FragmentSyncBinding

class SyncFragment : Fragment() {

    private var _binding: FragmentSyncBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: UrbanStyleDbHelper
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

        dbHelper = UrbanStyleDbHelper(requireContext())
        val registros = dbHelper.obtenerRegistrosLocales()

        adapter = VentaAdapter(registros)
        binding.rvVentasSync.adapter = adapter

        actualizarEstadoSync()

        binding.btnSincronizar.setOnClickListener {
            ejecutarSincronizacion()
        }
    }

    private fun actualizarEstadoSync() {
        val pendientes = dbHelper.obtenerCantidadPendientesSync()
        binding.tvPendientesSync.text = getString(R.string.text_pendientes_sync, pendientes)

        if (pendientes > 0) {
            binding.tvEstadoSync.text = "Existen $pendientes registros en SQLite esperando envío al servidor Spring Boot."
            binding.btnSincronizar.isEnabled = true
        } else {
            binding.tvEstadoSync.text = getString(R.string.text_sin_pendientes)
            binding.btnSincronizar.isEnabled = false
        }

        adapter.actualizarLista(dbHelper.obtenerRegistrosLocales())
    }

    private fun ejecutarSincronizacion() {
        val pendientes = dbHelper.obtenerCantidadPendientesSync()
        if (pendientes == 0) {
            Toast.makeText(requireContext(), "No hay datos pendientes para enviar", Toast.LENGTH_SHORT).show()
            return
        }

        binding.pbSyncLoading.visibility = View.VISIBLE
        binding.btnSincronizar.isEnabled = false

        /* Simulación / Llamada a consumo REST Retrofit hacia Spring Boot */
        Handler(Looper.getMainLooper()).postDelayed({
            if (_binding != null) {
                dbHelper.marcarComoSincronizadoAll()
                binding.pbSyncLoading.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    getString(R.string.text_sync_exito),
                    Toast.LENGTH_LONG
                ).show()
                actualizarEstadoSync()
            }
        }, 1800)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}