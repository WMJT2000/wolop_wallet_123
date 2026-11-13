package com.wolop.wallet.screens.home.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wolop.wallet.R
import com.wolop.wallet.Adapters.Home.LocalesAdapter
import com.wolop.wallet.data.mock.LocalesMockData

class LocalesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_locales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnAtrasLocales).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val recyclerLocales = view.findViewById<RecyclerView>(R.id.recyclerLocales)
        recyclerLocales.layoutManager = LinearLayoutManager(requireContext())
        recyclerLocales.adapter = LocalesAdapter().apply {
            submitLocales(LocalesMockData.locales)
        }
    }
}
