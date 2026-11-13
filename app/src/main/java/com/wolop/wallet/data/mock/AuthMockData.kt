package com.wolop.wallet.data.mock

// Cuenta de prueba: no hay backend, el login se valida contra estos valores.
object AuthMockData {

    const val CORREO = "prueba@wolop.com"
    const val CONTRASENA = "wolop123"

    // Datos que se guardan en Room la primera vez que se inicia sesión.
    const val NOMBRE_COMPLETO = "Micaela Alcocer"
    const val TELEFONO = "099 999 9999"

    fun credencialesValidas(correo: String, contrasena: String): Boolean =
        correo.trim().equals(CORREO, ignoreCase = true) && contrasena == CONTRASENA
}
