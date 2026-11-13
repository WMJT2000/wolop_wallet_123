package com.wolop.wallet.data.mock

data class Tarjeta(
    val id: String,
    val estado: String,
    val fechaVencimiento: String,
    val usoTotal: String,
    val nombre: String,
    val obsequio: String,
    val fechaValida: String,
    val saldo: String,
    // Contenido codificado en el QR de la tarjeta.
    val qrPayload: String
)

// Datos simulados para la pantalla de tarjeta.
object TarjetaMockData {
    val card = Tarjeta(
        id = "1234567891011",
        estado = "Activo",
        fechaVencimiento = "2026-12-20",
        usoTotal = "No",
        nombre = "Micaela",
        obsequio = "2 postres",
        fechaValida = "2028-12-02",
        saldo = "$20",
        qrPayload = "WOLOP-123456789"
    )
}
