package com.wolop.wallet.Adapters.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.wolop.wallet.R
import com.wolop.wallet.data.mock.Creation

class CreationsAdapter(
    private val creations: List<Creation>,
    private val itemWidthPx: Int
) : RecyclerView.Adapter<CreationsAdapter.CreationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_creacion, parent, false)
        val xml = view.layoutParams as? ViewGroup.MarginLayoutParams
        view.layoutParams = RecyclerView.LayoutParams(itemWidthPx, xml?.height ?: RecyclerView.LayoutParams.WRAP_CONTENT).apply {
            marginEnd = xml?.marginEnd ?: 0
        }
        return CreationViewHolder(view)
    }

    override fun onBindViewHolder(holder: CreationViewHolder, position: Int) {
        holder.bind(creations[position])
    }

    override fun getItemCount(): Int = creations.size

    class CreationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imgCreacion)

        fun bind(creation: Creation) {
            image.setImageResource(creation.imageRes)
            image.contentDescription = creation.name
        }
    }
}
