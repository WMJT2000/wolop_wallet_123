package com.wolop.wallet.data.mock

data class Tienda(
    val id: Int,
    val nombre: String,
    val ubicacion: String,
    val telefono: String,
    // Color de fondo de la tarjeta (paleta del menú de opciones).
    val cardColor: String
)

// Datos simulados para la pantalla de locales.
object LocalesMockData {
    val locales = listOf(
        Tienda(1, "Tienda 1", "Calle principal y calle secundaria", "09 999 9999", "#E0D6EB"),
        Tienda(2, "Tienda 2", "Calle principal y calle secundaria", "09 999 9999", "#DAEAD6"),
        Tienda(3, "Tienda 3", "Av. de los Shyris N34-120 y Naciones Unidas", "09 888 1111", "#F9E3E4"),
        Tienda(4, "Tienda 4", "Calle Larga 9-31 y Gran Colombia", "09 888 2222", "#DBE4EB"),
        Tienda(5, "Tienda 5", "Av. Amazonas N23-65 y Veintimilla", "09 888 3333", "#EBE6A3"),
        Tienda(6, "Tienda 6", "Calle Bolívar 12-34 y Olmedo", "09 888 4444", "#E0D6EB"),
        Tienda(7, "Tienda 7", "Av. Juan José Flores y Cottopaxi", "09 888 5555", "#DAEAD6"),
        Tienda(8, "Tienda 8", "Calle Cuenca 15-22 y Sucre", "09 888 6666", "#F9E3E4"),
        Tienda(9, "Tienda 9", "Av. 6 de Diciembre N33-110 y Eloy Alfaro", "09 888 7777", "#DBE4EB"),
        Tienda(10, "Tienda 10", "Calle Rocafuerte 10-05 y García Moreno", "09 888 8888", "#EBE6A3")
    )
}
