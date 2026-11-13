package com.wolop.wallet.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wolop.wallet.R
import com.wolop.wallet.data.AuthMockData
import com.wolop.wallet.data.DatabaseProvider
import com.wolop.wallet.data.local.entities.Usuario
import com.wolop.wallet.data.session.SessionManager
import com.wolop.wallet.screens.home.HomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val layoutCorreo = findViewById<TextInputLayout>(R.id.layoutCorreo)
        val layoutContrasena = findViewById<TextInputLayout>(R.id.layoutContrasena)
        val edtCorreo = findViewById<TextInputEditText>(R.id.edtCorreo)
        val edtContrasena = findViewById<TextInputEditText>(R.id.edtContrasena)
        val btnEntrar = findViewById<View>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val correo = edtCorreo.text?.toString()?.trim().orEmpty()
            val contrasena = edtContrasena.text?.toString().orEmpty()

            layoutCorreo.error = null
            layoutContrasena.error = null

            if (correo.isEmpty()) {
                layoutCorreo.error = "Ingresa tu correo"
                return@setOnClickListener
            }
            if (contrasena.isEmpty()) {
                layoutContrasena.error = "Ingresa tu contraseña"
                return@setOnClickListener
            }
            if (!AuthMockData.credencialesValidas(correo, contrasena)) {
                layoutContrasena.error = "Correo o contraseña incorrectos"
                return@setOnClickListener
            }

            iniciarSesion(AuthMockData.CORREO, btnEntrar)
        }

        findViewById<View>(R.id.btnCrearCuenta).setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }

    // Guarda la identidad en Room, abre la sesión y entra a los tabs.
    private fun iniciarSesion(correo: String, btnEntrar: View) {
        btnEntrar.isEnabled = false
        btnEntrar.alpha = 0.6f

        val repository = DatabaseProvider.getUsuarioRepository(this)

        lifecycleScope.launch {
            // Pequeña espera para simular la validación contra un servidor.
            delay(600)

            val id = try {
                val existente = repository.obtenerUsuarioPorCorreo(correo)
                if (existente == null) {
                    repository.insertarUsuario(
                        Usuario(
                            nombre = AuthMockData.NOMBRE_COMPLETO,
                            correo = correo,
                            telefono = AuthMockData.TELEFONO
                        )
                    ).toInt()
                } else {
                    // Mantiene la identidad alineada con la cuenta de prueba.
                    repository.actualizarUsuario(
                        existente.copy(
                            nombre = AuthMockData.NOMBRE_COMPLETO,
                            telefono = AuthMockData.TELEFONO
                        )
                    )
                    existente.id
                }
            } catch (e: Exception) {
                Log.e("WolopLogin", "No se pudo guardar el usuario de la sesión", e)
                -1
            }

            SessionManager(this@Login)
                .guardarSesion(id, correo, AuthMockData.NOMBRE_COMPLETO)

            startActivity(
                Intent(this@Login, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
        }
    }
}
