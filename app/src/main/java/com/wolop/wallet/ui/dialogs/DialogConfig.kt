package com.wolop.wallet.ui.dialogs

data class DialogConfig(
    val type: DialogType,
    val title: String,
    val message: String,
    val positiveText: String = type.defaultPositiveText,
    val negativeText: String? = type.defaultNegativeText
)
