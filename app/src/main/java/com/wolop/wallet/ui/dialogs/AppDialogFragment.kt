package com.wolop.wallet.ui.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.wolop.wallet.R

class AppDialogFragment : DialogFragment() {

    private var resultSent = false
    private var isLoadingDialog = false

    companion object {
        const val REQUEST_KEY = "wolop_dialog_result"
        const val KEY_ACTION = "action"
        const val ACTION_POSITIVE = "POSITIVE"
        const val ACTION_NEGATIVE = "NEGATIVE"
        const val ACTION_DISMISSED = "DISMISSED"

        private const val ARG_TYPE = "type"
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"
        private const val ARG_POSITIVE = "positive_text"
        private const val ARG_NEGATIVE = "negative_text"

        fun newInstance(config: DialogConfig): AppDialogFragment {
            return AppDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TYPE, config.type.name)
                    putString(ARG_TITLE, config.title)
                    putString(ARG_MESSAGE, config.message)
                    putString(ARG_POSITIVE, config.positiveText)
                    config.negativeText?.let { putString(ARG_NEGATIVE, it) }
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setCancelable(false)
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = (resources.displayMetrics.widthPixels * 0.88f).toInt()
            window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val type = runCatching {
            DialogType.valueOf(args.getString(ARG_TYPE) ?: DialogType.QUESTION.name)
        }.getOrDefault(DialogType.QUESTION)
        isLoadingDialog = type == DialogType.LOADING

        val config = DialogConfig(
            type = type,
            title = args.getString(ARG_TITLE).orEmpty(),
            message = args.getString(ARG_MESSAGE).orEmpty(),
            positiveText = args.getString(ARG_POSITIVE).orEmpty(),
            negativeText = args.getString(ARG_NEGATIVE)
        )

        view.findViewById<TextView>(R.id.txtTituloDialog).text = config.title
        view.findViewById<TextView>(R.id.txtMensajeDialog).text = config.message

        val btnPositive = view.findViewById<Button>(R.id.btnPositiveDialog)
        val btnNegative = view.findViewById<Button>(R.id.btnNegativeDialog)
        val buttonsRow = view.findViewById<LinearLayout>(R.id.botonesDialog)
        val divider = view.findViewById<View>(R.id.dividerDialog)
        val imgIcon = view.findViewById<ImageView>(R.id.imgIconDialog)
        val gifIcon = view.findViewById<GifLoadingView>(R.id.gifIconoCarga)

        btnPositive.text = config.positiveText
        val separador = view.findViewById<View>(R.id.separadorBotones)
        if (config.negativeText == null) {
            btnNegative.visibility = View.GONE
            separador.visibility = View.GONE
        } else {
            btnNegative.text = config.negativeText
            separador.visibility = View.VISIBLE
        }

        if (isLoadingDialog) {
            btnPositive.visibility = View.GONE
            btnNegative.visibility = View.GONE
            buttonsRow.visibility = View.GONE
            divider.visibility = View.GONE
            imgIcon.visibility = View.GONE
            gifIcon.visibility = View.VISIBLE
            gifIcon.start()
        } else {
            imgIcon.setImageResource(type.iconRes)
            gifIcon.visibility = View.GONE
            divider.visibility = View.VISIBLE
            buttonsRow.visibility = View.VISIBLE
        }

        btnPositive.setOnClickListener { onButtonClicked(ACTION_POSITIVE) }
        btnNegative.setOnClickListener { onButtonClicked(ACTION_NEGATIVE) }
    }

    private fun onButtonClicked(action: String) {
        if (resultSent) return
        resultSent = true

        view?.findViewById<Button>(R.id.btnPositiveDialog)?.apply {
            isEnabled = false
            alpha = 0.5f
        }
        view?.findViewById<Button>(R.id.btnNegativeDialog)?.apply {
            isEnabled = false
            alpha = 0.5f
        }

        deliverResult(action)
        dismiss()
    }

    private fun deliverResult(action: String) {
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY,
            Bundle().apply { putString(KEY_ACTION, action) }
        )
    }

    override fun onDestroyView() {
        val activity = activity
        // En un cambio de configuración (rotación) la vista se destruye y se recrea:
        // no hay cierre "sin elección", así que no se debe entregar DISMISSED.
        val changingConfig = activity?.isChangingConfigurations == true
        if (!resultSent && !isLoadingDialog && !changingConfig) {
            resultSent = true
            deliverResult(ACTION_DISMISSED)
        }
        view?.findViewById<GifLoadingView>(R.id.gifIconoCarga)?.stop()
        super.onDestroyView()
    }
}
