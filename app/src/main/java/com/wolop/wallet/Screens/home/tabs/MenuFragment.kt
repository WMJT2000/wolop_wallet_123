package com.wolop.wallet.screens.home.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wolop.wallet.R
import com.wolop.wallet.screens.home.HomeActivity

class MenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.btnAtrasMenu).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        view.findViewById<View>(R.id.cardTarjeta).setOnClickListener {
            (requireActivity() as HomeActivity).abrirSubPantalla(TarjetaFragment())
        }
        view.findViewById<View>(R.id.cardLocales).setOnClickListener {
            (requireActivity() as HomeActivity).abrirSubPantalla(LocalesFragment())
        }
        view.findViewById<View>(R.id.cardContactos).setOnClickListener {
            (requireActivity() as HomeActivity).abrirSubPantalla(ContactosFragment())
        }
    }
}
