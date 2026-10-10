package pe.edu.cibertec.grupo10.urbanstyle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import pe.edu.cibertec.grupo10.urbanstyle.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardHomeCatalogo.setOnClickListener {
            findNavController().navigate(R.id.catalogoFragment)
        }

        binding.cardHomeVentas.setOnClickListener {
            findNavController().navigate(R.id.registroVentaFragment)
        }

        binding.cardHomeReservas.setOnClickListener {
            findNavController().navigate(R.id.reservasFragment)
        }

        binding.cardHomeSync.setOnClickListener {
            findNavController().navigate(R.id.syncFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}