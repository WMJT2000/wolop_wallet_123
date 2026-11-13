
package com.wolop.wallet.data.qr

import android.graphics.Color

data class QRConfig(

    // Dimensiones
    val tamaño: Int = 512,

    // Colores
    val colorPrincipal: Int = Color.BLACK,
    val colorFondo: Int = Color.WHITE,

    // Estilo visual
    val estilo: QRStyle = QRStyle.CLASSIC,

    // Margen exterior
    val margen: Int = 1,

    // Degradado opcional
    val usarDegradado: Boolean = false,
    val colorDegradado: Int = Color.BLUE,

    // Logo opcional
    val logo: android.graphics.Bitmap? = null,
    val tamañoLogo: Float = 0.20f
)
