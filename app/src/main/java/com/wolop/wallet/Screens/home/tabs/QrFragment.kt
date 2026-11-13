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
import com.wolop.wallet.data.HomeMockData
import com.wolop.wallet.data.qr.QRConfig
import com.wolop.wallet.data.qr.QRGenerator
import com.wolop.wallet.data.qr.QRStyle
import com.wolop.wallet.data.session.SessionManager

class QrFragment : Fragment() {

    private companion object {
        // Dato quemado que se codifica en el QR.
        const val CODIGO_QUEMADO = "WOLOP-000185"
        const val COLOR_QR = "#3A2214"
        const val FONDO_QR = "#FAF5EF"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuario = HomeMockData.user
        val session = SessionManager(requireContext())

        val nombre = session.nombre()
            ?.takeIf { it.isNotBlank() }
            ?: "${usuario.name} ${usuario.lastName}".trim()

        view.findViewById<TextView>(R.id.txtNombreQr).text = nombre
        view.findViewById<TextView>(R.id.txtPetalesQr).text = usuario.petals.toString()

        view.findViewById<View>(R.id.btnAtrasQr).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        generarQr(view, CODIGO_QUEMADO)
    }

    private fun generarQr(view: View, contenido: String) {
        val imageQR = view.findViewById<ImageView>(R.id.imgCodigoQR)
        val fondo = Color.parseColor(FONDO_QR)
        imageQR.setBackgroundColor(fondo)

        try {
            val qr = QRGenerator.generar(
                contenido = contenido,
                config = QRConfig(
                    tamaño = 640,
                    colorPrincipal = Color.parseColor(COLOR_QR),
                    colorFondo = fondo,
                    estilo = QRStyle.CLASSIC,
                    margen = 1
                )
            )
            imageQR.setImageBitmap(qr)
        } catch (e: Exception) {
            Log.e("WolopQr", "No se pudo generar el código QR", e)
        }
    }
}
