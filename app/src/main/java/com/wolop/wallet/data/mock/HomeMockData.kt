package com.wolop.wallet.data.mock

import com.wolop.wallet.R
import com.wolop.wallet.data.local.entities.Usuario

data class HomeUser(
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val balance: Double,
    val petals: Int,
    val targetPetals: Int,
    val nextBenefit: String
)

data class Creation(
    val name: String,
    val imageRes: Int
)

// Datos simulados para la pantalla de inicio.
object HomeMockData {
    val user = HomeUser(
        name = "Micaela",
        lastName = "Alcocer",
        email = "micaela@gmail.com",
        phone = "099 999 9999",
        balance = 0.0,
        petals = 185,
        targetPetals = 300,
        nextBenefit = "Petal individual gratis"
    )

    val creations = listOf(
        Creation("Selva negra", R.drawable.img_product2),
        Creation("Tarta de fresas", R.drawable.img_product3),
        Creation("Tarta de chocolate", R.drawable.img_product4)
    )

    // Mezcla la identidad guardada en Room con los valores visuales del mock.
    fun conIdentidad(usuario: Usuario?): HomeUser {
        if (usuario == null) return user

        val partes = usuario.nombre.trim().split(" ").filter { it.isNotBlank() }
        val nombre = partes.firstOrNull() ?: user.name
        val apellido = partes.drop(1).joinToString(" ").ifBlank { user.lastName }

        return user.copy(
            name = nombre,
            lastName = apellido,
            email = usuario.correo,
            phone = usuario.telefono ?: user.phone
        )
    }
}
