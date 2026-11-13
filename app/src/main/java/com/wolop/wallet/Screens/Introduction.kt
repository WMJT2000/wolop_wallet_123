package com.wolop.wallet.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.wolop.wallet.Adapters.Introduction.IntroductionAdapter
import com.wolop.wallet.R

class Introduction : AppCompatActivity() {

    private val banners by lazy { loadBanners() }
    private val dots = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.screen_introduction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (banners.isEmpty()) {
            goToLogin()
            return
        }

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerIntroduction)
        val btnSiguiente = findViewById<ImageButton>(R.id.btnSiguiente)
        val btnSaltar = findViewById<View>(R.id.btnSaltar)
        val indicatorsContainer = findViewById<LinearLayout>(R.id.layoutIndicadores)

        setupIndicators(indicatorsContainer)
        viewPager.adapter = IntroductionAdapter(banners)

        fun updateIndicators(position: Int) {
            dots.forEachIndexed { index, dot ->
                dot.setBackgroundResource(
                    if (index == position) R.drawable.bg_indicador_activo
                    else R.drawable.bg_indicador_inactivo
                )
            }
            val isLast = position == banners.lastIndex
            btnSiguiente.setImageResource(
                if (isLast) R.drawable.icon_intro_ok else R.drawable.icon_intro_next
            )
        }

        updateIndicators(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateIndicators(position)
            }
        })

        btnSiguiente.setOnClickListener {
            val next = viewPager.currentItem + 1
            if (next < banners.size) {
                viewPager.currentItem = next
            } else {
                goToLogin()
            }
        }

        btnSaltar.setOnClickListener { goToLogin() }
    }

    private fun loadBanners(): List<Int> {
        val result = mutableListOf<Int>()

        // banner1, banner2, banner3, ...
        var i = 1
        while (true) {
            val id = resources.getIdentifier("banner$i", "drawable", packageName)
            if (id == 0) break
            result.add(id)
            i++
        }

        // Otros drawables que empiecen con "banner" (si no siguen el patrón numerado)
        if (result.isEmpty()) {
            R.drawable::class.java.fields
                .filter { it.name.startsWith("banner") }
                .sortedBy { it.name }
                .forEach { field ->
                    runCatching { result.add(field.getInt(null)) }
                }
        }

        return result
    }

    private fun setupIndicators(container: LinearLayout) {
        container.removeAllViews()
        dots.clear()

        val size = dpToPx(9)
        val margin = dpToPx(4)
        banners.indices.forEach { index ->
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginStart = margin
                    marginEnd = margin
                }
                setBackgroundResource(
                    if (index == 0) R.drawable.bg_indicador_activo
                    else R.drawable.bg_indicador_inactivo
                )
            }
            container.addView(dot)
            dots.add(dot)
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    private fun goToLogin() {
        startActivity(Intent(this, Login::class.java))
        finish()
    }
}