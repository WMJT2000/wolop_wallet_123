package com.wolop.wallet.data.session

import android.content.Context

// Sesión simulada: se guarda en SharedPreferences para sobrevivir al cierre de la app.
class SessionManager(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun guardarSesion(id: Int, correo: String, nombre: String) {
        prefs.edit()
            .putBoolean(KEY_LOGGED, true)
            .putInt(KEY_USER_ID, id)
            .putString(KEY_CORREO, correo)
            .putString(KEY_NOMBRE, nombre)
            .commit()
    }

    fun estaLogueado(): Boolean =
        prefs.getBoolean(KEY_LOGGED, false) && !correo().isNullOrBlank()

    fun id(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun correo(): String? = prefs.getString(KEY_CORREO, null)

    fun nombre(): String? = prefs.getString(KEY_NOMBRE, null)

    fun cerrarSesion() {
        prefs.edit().clear().commit()
    }

    private companion object {
        const val PREFS = "wolop_session"
        const val KEY_LOGGED = "logged_in"
        const val KEY_USER_ID = "user_id"
        const val KEY_CORREO = "user_correo"
        const val KEY_NOMBRE = "user_nombre"
    }
}
