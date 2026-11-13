package com.wolop.wallet.ui.dialogs

import com.wolop.wallet.R

enum class DialogType(
    val iconRes: Int,
    val defaultPositiveText: String,
    val defaultNegativeText: String?
) {
    SUCCESS(R.drawable.icon_alert_success, "Entendido", null),
    ERROR(R.drawable.icon_alert_error, "Entendido", null),
    ALERT(R.drawable.icon_alert_warning, "Entendido", null),
    QUESTION(R.drawable.icon_alert_question, "Aceptar", "Cancelar"),
    LOADING(0, "", null)
}
