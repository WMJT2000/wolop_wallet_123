
package com.wolop.wallet.data.qr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QRGenerator {

    fun generar(
        contenido: String,
        config: QRConfig = QRConfig()
    ): Bitmap {

        require(contenido.isNotBlank()) {
            "El contenido no puede estar vacío"
        }

        require(config.tamaño > 0) {
            "El tamaño debe ser mayor que cero"
        }

        require(config.margen >= 0) {
            "El margen no puede ser negativo"
        }

        require(config.tamañoLogo in 0f..0.25f) {
            "El tamaño del logo debe estar entre 0 y 0.25"
        }

        if (config.logo != null) {
            require(config.logo.width > 0 && config.logo.height > 0) {
                "El logo no es válido"
            }
        }

        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
            EncodeHintType.MARGIN to config.margen
        )

        val matriz = MultiFormatWriter().encode(
            contenido,
            BarcodeFormat.QR_CODE,
            config.tamaño,
            config.tamaño,
            hints
        )

        val ancho = matriz.width
        val alto = matriz.height

        val bitmap = Bitmap.createBitmap(
            ancho,
            alto,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        canvas.drawColor(config.colorFondo)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val colorFinal = config.colorPrincipal
        val colorSecundario = config.colorDegradado

        val shader = if (config.usarDegradado) {
            android.graphics.LinearGradient(
                0f,
                0f,
                ancho.toFloat(),
                alto.toFloat(),
                colorFinal,
                colorSecundario,
                android.graphics.Shader.TileMode.CLAMP
            )
        } else null

        paint.shader = shader
        paint.color = colorFinal

        val celdaAncho = ancho.toFloat() / matriz.width
        val celdaAlto = alto.toFloat() / matriz.height

        for (x in 0 until matriz.width) {
            for (y in 0 until matriz.height) {

                if (!matriz[x, y]) continue

                val izquierda = x * celdaAncho
                val arriba = y * celdaAlto
                val derecha = izquierda + celdaAncho
                val abajo = arriba + celdaAlto

                when (config.estilo) {

                    QRStyle.CLASSIC -> {
                        canvas.drawRect(
                            izquierda,
                            arriba,
                            derecha,
                            abajo,
                            paint
                        )
                    }

                    QRStyle.ROUNDED -> {
                        val rect = RectF(
                            izquierda + 0.5f,
                            arriba + 0.5f,
                            derecha - 0.5f,
                            abajo - 0.5f
                        )

                        canvas.drawRoundRect(
                            rect,
                            celdaAncho * 0.30f,
                            celdaAlto * 0.30f,
                            paint
                        )
                    }

                    QRStyle.DOTS -> {
                        canvas.drawCircle(
                            izquierda + celdaAncho / 2,
                            arriba + celdaAlto / 2,
                            minOf(celdaAncho, celdaAlto) * 0.38f,
                            paint
                        )
                    }
                }
            }
        }

        paint.shader = null

        // Dibujar logo centrado, si existe
        config.logo?.let { logo ->

            val lado = ancho * config.tamañoLogo

            val left = (ancho - lado) / 2
            val top = (alto - lado) / 2

            val logoRect = RectF(
                left,
                top,
                left + lado,
                top + lado
            )

            val fondoLogo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = config.colorFondo
            }

            val margenLogo = lado * 0.12f

            canvas.drawRoundRect(
                RectF(
                    left - margenLogo,
                    top - margenLogo,
                    left + lado + margenLogo,
                    top + lado + margenLogo
                ),
                16f,
                16f,
                fondoLogo
            )

            canvas.drawBitmap(
                logo,
                null,
                logoRect,
                Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            )
        }

        return bitmap
    }
}
