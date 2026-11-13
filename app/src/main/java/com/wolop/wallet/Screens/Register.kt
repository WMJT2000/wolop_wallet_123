package com.wolop.wallet.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wolop.wallet.R

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val goToLogin = View.OnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
        findViewById<View>(R.id.txtEntrar).setOnClickListener(goToLogin)
        findViewById<View>(R.id.txtYaTienesCuenta).setOnClickListener(goToLogin)
    }
}