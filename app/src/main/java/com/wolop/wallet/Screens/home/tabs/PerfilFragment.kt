package com.wolop.wallet.screens.home.tabs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.wolop.wallet.R
import com.wolop.wallet.data.mock.HomeMockData
import com.wolop.wallet.data.repository.DatabaseProvider
import com.wolop.wallet.data.mock.HomeUser
import com.wolop.wallet.data.session.SessionManager
import com.wolop.wallet.screens.Welcome
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root = view

        // Se pinta el mock mientras se resuelve la identidad guardada.
        bind(HomeMockData.user)
        cargarIdentidad()
        setupAcciones(view)
    }

    override fun onDestroyView() {
        root = null
        super.onDestroyView()
    }

    private fun bind(user: HomeUser) {
        val view = root ?: return

        view.findViewById<TextView>(R.id.txtNombrePerfil).text =
            "${user.name} ${user.lastName}".trim()
        view.findViewById<TextView>(R.id.txtCorreoPerfil).text = user.email
        view.findViewById<TextView>(R.id.txtTelefonoPerfil).text = user.phone
        view.findViewById<TextView>(R.id.txtPetalesPerfil).text = user.petals.toString()
    }

    // Lee el usuario guardado en Room al iniciar sesión.
    private fun cargarIdentidad() {
        val session = SessionManager(requireContext())
        val correo = session.correo() ?: return
        val contexto = context ?: return
        val repository = DatabaseProvider.getUsuarioRepository(contexto)

        viewLifecycleOwner.lifecycleScope.launch {
            val usuario = try {
                repository.obtenerUsuarioPorCorreo(correo)
            } catch (e: Exception) {
                Log.e("WolopPerfil", "No se pudo leer el usuario", e)
                null
            }
            bind(HomeMockData.conIdentidad(usuario))
        }
    }

    private fun setupAcciones(view: View) {
        view.findViewById<View>(R.id.btnAtrasPerfil).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        view.findViewById<View>(R.id.filaCerrarSesion).setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion() {
        val contexto = context ?: return
        SessionManager(contexto).cerrarSesion()

        startActivity(
            Intent(contexto, Welcome::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        requireActivity().finish()
    }
}
