package com.wolop.wallet.data.mock

import com.wolop.wallet.R

data class Benefit(
    val id: Int,
    val name: String,
    val petals: Int,
    val imageRes: Int,
    val redeemed: Boolean = false
)

// Datos simulados para la pantalla de beneficios.
object BenefitMockData {
    val benefits = mutableListOf(
        Benefit(1, "Café individual", 200, R.drawable.icon_benefit3),
        Benefit(2, "Tiramisú individual", 250, R.drawable.icon_benefit2),
        Benefit(3, "Tarta individual", 350, R.drawable.icon_benefit1),
        Benefit(4, "Torta individual", 400, R.drawable.icon_benefit4),
        Benefit(5, "Café doble", 300, R.drawable.icon_benefit6, redeemed = true),
        Benefit(6, "Postre de temporada", 450, R.drawable.icon_benefit5, redeemed = true),
        Benefit(7, "Selva negra", 500, R.drawable.img_product2),
        Benefit(8, "Tarta de fresas", 380, R.drawable.img_product3),
        Benefit(9, "Tarta de chocolate", 425, R.drawable.img_product4)
    )

    fun redeem(benefit: Benefit) {
        val index = benefits.indexOfFirst { it.id == benefit.id }
        if (index >= 0) {
            benefits[index] = benefits[index].copy(redeemed = true)
        }
    }
}
