package com.wolop.wallet.screens.home.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.wolop.wallet.Adapters.Home.CreationsAdapter
import com.wolop.wallet.R
import com.wolop.wallet.data.mock.HomeMockData
import com.wolop.wallet.data.session.SessionManager
import java.util.Locale
import kotlin.math.roundToInt

class InicioFragment : Fragment() {

    private val dots = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUserData(view)
        setupCreations(view)
    }

    private fun bindUserData(view: View) {
        val session = SessionManager(requireContext())
        val nombreCuenta = session.nombre()?.trim()?.split(" ")?.firstOrNull().orEmpty()
        val user = if (nombreCuenta.isBlank()) {
            HomeMockData.user
        } else {
            HomeMockData.user.copy(name = nombreCuenta)
        }

        view.findViewById<TextView>(R.id.txtSaludo).text = "Buenos días, ${user.name}"
        view.findViewById<TextView>(R.id.txtSaldo).text =
            String.format(Locale.US, "\$%.2f", user.balance)
        view.findViewById<TextView>(R.id.txtPetales).text = user.petals.toString()
        view.findViewById<TextView>(R.id.txtBeneficio).text = user.nextBenefit
        view.findViewById<TextView>(R.id.txtFaltan).text =
            "Faltan: ${(user.targetPetals - user.petals).coerceAtLeast(0)} pétalos."

        val progress = if (user.targetPetals > 0) {
            (user.petals * 100f / user.targetPetals).roundToInt()
        } else {
            0
        }
        view.findViewById<LinearProgressIndicator>(R.id.progressBeneficio)
            .setProgressCompat(progress.coerceIn(0, 100), false)
    }

    private fun setupCreations(view: View) {
        val creations = HomeMockData.creations
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerCreaciones)
        val indicators = view.findViewById<LinearLayout>(R.id.layoutIndicadoresCreaciones)

        if (creations.isEmpty()) {
            recycler.isVisible = false
            indicators.isVisible = false
            return
        }

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recycler.layoutManager = layoutManager
        recycler.adapter = CreationsAdapter(creations)

        setupIndicators(indicators, creations.size)

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = layoutManager.findFirstVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    updateIndicators(position)
                }
            }
        })
    }

    private fun setupIndicators(container: LinearLayout, count: Int) {
        container.removeAllViews()
        dots.clear()

        val size = dpToPx(9)
        val margin = dpToPx(4)
        repeat(count) { index ->
            val dot = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginStart = margin
                    marginEnd = margin
                }
                setBackgroundResource(
                    if (index == 0) R.drawable.bg_indicador_activo
                    else R.drawable.bg_indicador_inactivo
                )
            }
            container.addView(dot)
            dots.add(dot)
        }
    }

    private fun updateIndicators(position: Int) {
        dots.forEachIndexed { index, dot ->
            dot.setBackgroundResource(
                if (index == position) R.drawable.bg_indicador_activo
                else R.drawable.bg_indicador_inactivo
            )
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()
}
