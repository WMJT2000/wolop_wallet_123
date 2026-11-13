package com.wolop.wallet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wolop.wallet.data.session.SessionManager
import com.wolop.wallet.screens.Welcome
import com.wolop.wallet.screens.home.HomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Con sesión activa se entra directo a los tabs de inicio.
        val destino = if (SessionManager(this).estaLogueado()) {
            HomeActivity::class.java
        } else {
            Welcome::class.java
        }

        startActivity(Intent(this, destino))
        finish()
    }
}
