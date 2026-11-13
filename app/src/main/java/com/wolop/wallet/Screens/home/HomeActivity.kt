package com.wolop.wallet.screens.home

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.wolop.wallet.R
import com.wolop.wallet.data.session.SessionManager
import com.wolop.wallet.screens.Welcome
import com.wolop.wallet.screens.home.tabs.BeneficiosFragment
import com.wolop.wallet.screens.home.tabs.InicioFragment
import com.wolop.wallet.screens.home.tabs.MenuFragment
import com.wolop.wallet.screens.home.tabs.PerfilFragment
import com.wolop.wallet.screens.home.tabs.QrFragment

class HomeActivity : AppCompatActivity() {

    private var selectedNavId = R.id.navInicio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sin sesión no se puede entrar a los tabs.
        if (!SessionManager(this).estaLogueado()) {
            startActivity(
                android.content.Intent(this, Welcome::class.java).apply {
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                        android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        selectedNavId = savedInstanceState?.getInt(KEY_SELECTED_NAV, R.id.navInicio)
            ?: R.id.navInicio

        setupBottomNavigation(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_NAV, selectedNavId)
    }

    private fun setupBottomNavigation(savedInstanceState: Bundle?) {
        listOf(R.id.navInicio, R.id.navBeneficios, R.id.navQr, R.id.navMenu, R.id.navPerfil)
            .forEach { navId ->
                findViewById<View>(navId).setOnClickListener { selectNav(navId) }
            }

        if (savedInstanceState == null) {
            openTab(createFragment(selectedNavId))
        }
        updateNavigationSelection()

        onBackPressedDispatcher.addCallback(this) {
            when {
                supportFragmentManager.backStackEntryCount > 0 ->
                    supportFragmentManager.popBackStack()

                selectedNavId != R.id.navInicio -> selectNav(R.id.navInicio)

                else -> {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun selectNav(navId: Int) {
        if (selectedNavId == navId) {
            // Si estamos en una sub-pantalla (ej. Tarjeta desde Menú), volver a la tab.
            clearSubScreens()
            return
        }
        selectedNavId = navId
        updateNavigationSelection()
        clearSubScreens()
        openTab(createFragment(navId))
    }

    private fun clearSubScreens() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    internal fun abrirSubPantalla(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFragmentContainer, fragment)
            .addToBackStack(fragment::class.java.name)
            .commit()
    }

    private fun updateNavigationSelection() {
        listOf(R.id.navInicio, R.id.navBeneficios, R.id.navQr, R.id.navMenu, R.id.navPerfil)
            .forEach { navId ->
                findViewById<View>(navId).isSelected = navId == selectedNavId
            }
    }

    private fun createFragment(navId: Int): Fragment = when (navId) {
        R.id.navBeneficios -> BeneficiosFragment()
        R.id.navQr -> QrFragment()
        R.id.navMenu -> MenuFragment()
        R.id.navPerfil -> PerfilFragment()
        else -> InicioFragment()
    }

    private fun openTab(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.homeFragmentContainer, fragment)
            .commit()
    }

    private companion object {
        const val KEY_SELECTED_NAV = "selected_nav"
    }
}
