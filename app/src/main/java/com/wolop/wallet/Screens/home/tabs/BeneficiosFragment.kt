package com.wolop.wallet.screens.home.tabs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wolop.wallet.Adapters.Home.BenefitsAdapter
import com.wolop.wallet.R
import com.wolop.wallet.data.mock.Benefit
import com.wolop.wallet.data.mock.BenefitMockData

class BeneficiosFragment : Fragment() {

    private lateinit var adapter: BenefitsAdapter
    private lateinit var txtTabDisponibles: TextView
    private lateinit var txtTabCanjeados: TextView
    private lateinit var lineaDisponibles: View
    private lateinit var lineaCanjeados: View
    private lateinit var txtVacio: TextView

    private var showAvailable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beneficios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BenefitsAdapter { benefit -> canjear(benefit) }

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerBeneficios)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        txtTabDisponibles = view.findViewById(R.id.txtTabDisponibles)
        txtTabCanjeados = view.findViewById(R.id.txtTabCanjeados)
        lineaDisponibles = view.findViewById(R.id.lineaDisponibles)
        lineaCanjeados = view.findViewById(R.id.lineaCanjeados)
        txtVacio = view.findViewById(R.id.txtBeneficiosVacio)

        view.findViewById<View>(R.id.btnAtras).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        view.findViewById<View>(R.id.tabDisponibles)
            .setOnClickListener { selectTab(disponibles = true) }
        view.findViewById<View>(R.id.tabCanjeados)
            .setOnClickListener { selectTab(disponibles = false) }

        selectTab(disponibles = true)
    }

    private fun selectTab(disponibles: Boolean) {
        showAvailable = disponibles

        val activo = Color.parseColor("#C1974E")
        val inactivo = Color.parseColor("#412939")

        txtTabDisponibles.setTextColor(if (disponibles) activo else inactivo)
        txtTabCanjeados.setTextColor(if (disponibles) inactivo else activo)
        lineaDisponibles.isInvisible = !disponibles
        lineaCanjeados.isInvisible = disponibles

        loadBenefits()
    }

    private fun loadBenefits() {
        val benefits = BenefitMockData.benefits.filter { it.redeemed != showAvailable }
        adapter.submitBenefits(benefits)

        txtVacio.isVisible = benefits.isEmpty()
        txtVacio.text = if (showAvailable) {
            "No tienes beneficios disponibles."
        } else {
            "Aún no has canjeado beneficios."
        }
    }

    private fun canjear(benefit: Benefit) {
        BenefitMockData.redeem(benefit)
        Toast.makeText(requireContext(), "Canjeaste: ${benefit.name}", Toast.LENGTH_SHORT).show()
        loadBenefits()
    }
}
