package com.wolop.wallet.ui.dialogs

import androidx.fragment.app.FragmentManager

object DialogManager {

    private const val TAG_DEFAULT = "app_dialog"
    const val TAG_LOADING = "app_dialog_loading"

    /**
     * Muestra el diálogo si no hay estado guardado.
     * @return true si se mostró, false si se omitió por estado guardado o por duplicado.
     */
    fun show(fragmentManager: FragmentManager, config: DialogConfig): Boolean {
        if (fragmentManager.isStateSaved) return false
        val tag = if (config.type == DialogType.LOADING) TAG_LOADING else TAG_DEFAULT
        if (fragmentManager.findFragmentByTag(tag) != null) return false
        AppDialogFragment.newInstance(config).show(fragmentManager, tag)
        return true
    }

    fun dismissLoading(fragmentManager: FragmentManager) {
        val loading = fragmentManager.findFragmentByTag(TAG_LOADING) as? AppDialogFragment ?: return
        loading.dismiss()
    }
}
