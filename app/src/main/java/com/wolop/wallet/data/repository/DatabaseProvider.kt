
package com.wolop.wallet.data.repository

import android.content.Context
import com.wolop.wallet.data.local.AppDatabase

object DatabaseProvider {

    fun getUsuarioRepository(context: Context): UsuarioRepository {

        val database = AppDatabase.getDatabase(
            context.applicationContext
        )

        return UsuarioRepository(
            database.usuarioDao()
        )
    }
}
