package com.wolop.wallet.Adapters.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.wolop.wallet.R
import com.wolop.wallet.data.mock.Benefit

class BenefitsAdapter(
    private val onCanjear: (Benefit) -> Unit
) : RecyclerView.Adapter<BenefitsAdapter.BenefitViewHolder>() {

    private val items = mutableListOf<Benefit>()

    fun submitBenefits(benefits: List<Benefit>) {
        items.clear()
        items.addAll(benefits)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BenefitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_beneficio, parent, false)
        return BenefitViewHolder(view)
    }

    override fun onBindViewHolder(holder: BenefitViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class BenefitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imgBeneficio)
        private val name: TextView = itemView.findViewById(R.id.txtNombreBeneficio)
        private val petals: TextView = itemView.findViewById(R.id.txtPetalosBeneficio)
        private val redeem: TextView = itemView.findViewById(R.id.btnCanjearBeneficio)

        fun bind(benefit: Benefit) {
            image.setImageResource(benefit.imageRes)
            image.contentDescription = benefit.name
            name.text = benefit.name
            petals.text = "${benefit.petals} pétalos"

            // El botón solo se muestra mientras el beneficio no haya sido canjeado.
            redeem.isVisible = !benefit.redeemed
            redeem.setOnClickListener { onCanjear(benefit) }
        }
    }
}
