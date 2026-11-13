package com.wolop.wallet.Adapters.Home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.wolop.wallet.R
import com.wolop.wallet.data.mock.Tienda

class LocalesAdapter : RecyclerView.Adapter<LocalesAdapter.LocalViewHolder>() {

    private val items = mutableListOf<Tienda>()

    fun submitLocales(tiendas: List<Tienda>) {
        items.clear()
        items.addAll(tiendas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_local, parent, false)
        return LocalViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class LocalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView as MaterialCardView
        private val nombre: TextView = itemView.findViewById(R.id.txtLocalNombre)
        private val ubicacion: TextView = itemView.findViewById(R.id.txtLocalUbicacion)
        private val telefono: TextView = itemView.findViewById(R.id.txtLocalTelefono)

        fun bind(tienda: Tienda) {
            card.setCardBackgroundColor(Color.parseColor(tienda.cardColor))
            nombre.text = tienda.nombre
            ubicacion.text = tienda.ubicacion
            telefono.text = tienda.telefono
        }
    }
}
