package com.wolop.wallet.screens.home.tabs

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.wolop.wallet.R
import com.wolop.wallet.data.mock.TarjetaMockData
import com.wolop.wallet.data.qr.QRConfig
import com.wolop.wallet.data.qr.QRGenerator
import com.wolop.wallet.data.qr.QRStyle
import com.wolop.wallet.data.session.SessionManager

class TarjetaFragment : Fragment() {

    private companion object {
        // Estilo del QR (igual que QrFragment).
        const val COLOR_QR = "#3A2214"
        const val FONDO_QR = "#FAF5EF"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tarjeta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val card = TarjetaMockData.card
        val nombreSesion = SessionManager(requireContext()).nombre()?.takeIf { it.isNotBlank() }

        view.findViewById<TextView>(R.id.txtIdTarjeta).text = "ID: ${card.id}"
        view.findViewById<TextView>(R.id.txtEstadoValor).text = card.estado
        view.findViewById<TextView>(R.id.txtFechaVencimientoValor).text = card.fechaVencimiento
        view.findViewById<TextView>(R.id.txtUsoTotalValor).text = card.usoTotal

        view.findViewById<TextView>(R.id.txtNombreValor).text = nombreSesion ?: card.nombre
        view.findViewById<TextView>(R.id.txtObsequioValor).text = card.obsequio
        view.findViewById<TextView>(R.id.txtFechaValidaValor).text = card.fechaValida
        view.findViewById<TextView>(R.id.txtSaldoValor).text = card.saldo

        view.findViewById<View>(R.id.btnAtrasTarjeta).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        generarQr(view, card.qrPayload)
    }

    private fun generarQr(view: View, contenido: String) {
        val imageQR = view.findViewById<ImageView>(R.id.imgCodigoTarjeta)
        val fondo = Color.parseColor(FONDO_QR)

        try {
            val qr = QRGenerator.generar(
                contenido = contenido,
                config = QRConfig(
                    tamaño = 512,
                    colorPrincipal = Color.parseColor(COLOR_QR),
                    colorFondo = fondo,
                    estilo = QRStyle.CLASSIC,
                    margen = 1
                )
            )
            imageQR.setImageBitmap(qr)
        } catch (e: Exception) {
            Log.e("WolopTarjeta", "No se pudo generar el código QR", e)
        }
    }
}
