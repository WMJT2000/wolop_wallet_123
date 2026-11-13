package com.wolop.wallet.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wolop.wallet.R
import com.wolop.wallet.ui.dialogs.AppDialogFragment
import com.wolop.wallet.ui.dialogs.DialogConfig
import com.wolop.wallet.ui.dialogs.DialogManager
import com.wolop.wallet.ui.dialogs.DialogType

class DemoDialogsActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val loadingRunnable = Runnable { completeLoading() }

    private var loadingFinishAt = 0L

    companion object {
        private const val KEY_LOADING_END = "loading_end"
        private const val LOADING_DURATION_MS = 2_500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_demo_dialogs)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        savedInstanceState?.let { state ->
            loadingFinishAt = state.getLong(KEY_LOADING_END, 0L)
        }

        supportFragmentManager.setFragmentResultListener(
            AppDialogFragment.REQUEST_KEY,
            this
        ) { _, bundle ->
            val action = bundle.getString(AppDialogFragment.KEY_ACTION) ?: "—"
            findViewById<android.widget.TextView>(R.id.txtResultadoDemo).text =
                "Último resultado: $action"
        }

        bindCard(R.id.cardDemoExito) {
            DialogManager.show(
                supportFragmentManager,
                DialogConfig(DialogType.SUCCESS, "¡Listo!", "La operación se completó correctamente.")
            )
        }

        bindCard(R.id.cardDemoError) {
            DialogManager.show(
                supportFragmentManager,
                DialogConfig(DialogType.ERROR, "Algo salió mal", "No se pudo completar la operación. Intente nuevamente.")
            )
        }

        bindCard(R.id.cardDemoAviso) {
            DialogManager.show(
                supportFragmentManager,
                DialogConfig(DialogType.ALERT, "Atención", "Esta acción requiere su confirmación antes de continuar.")
            )
        }

        bindCard(R.id.cardDemoPregunta) {
            DialogManager.show(
                supportFragmentManager,
                DialogConfig(DialogType.QUESTION, "Confirmar acción", "¿Deseas eliminar este registro? Esta acción no se puede deshacer.")
            )
        }

        bindCard(R.id.cardDemoCarga) { startLoading() }

        findViewById<View>(R.id.btnAtrasDemo).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Reanudar un temporizador que estuviera corriendo al recrearse la activity.
        if (loadingFinishAt != 0L) {
            val remaining = loadingFinishAt - System.currentTimeMillis()
            if (remaining <= 0L) {
                completeLoading()
            } else {
                handler.postDelayed(loadingRunnable, remaining)
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (loadingFinishAt != 0L) {
                // No cancelar la demostración en curso: ignorar el back durante la carga.
                return@addCallback
            }
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_LOADING_END, loadingFinishAt)
    }

    override fun onDestroy() {
        handler.removeCallbacks(loadingRunnable)
        super.onDestroy()
    }

    private fun bindCard(id: Int, action: () -> Unit) {
        findViewById<View>(id).setOnClickListener { action() }
    }

    private fun startLoading() {
        if (loadingFinishAt != 0L) return
        val shown = DialogManager.show(
            supportFragmentManager,
            DialogConfig(DialogType.LOADING, "Cargando", "Espere un momento…")
        )
        if (!shown) return
        loadingFinishAt = System.currentTimeMillis() + LOADING_DURATION_MS
        handler.postDelayed(loadingRunnable, LOADING_DURATION_MS)
    }

    private fun completeLoading() {
        loadingFinishAt = 0L
        DialogManager.dismissLoading(supportFragmentManager)
        DialogManager.show(
            supportFragmentManager,
            DialogConfig(DialogType.SUCCESS, "¡Listo!", "La operación se completó correctamente.")
        )
    }
}
