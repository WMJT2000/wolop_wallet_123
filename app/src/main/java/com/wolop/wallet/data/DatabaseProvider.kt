
package com.wolop.wallet.data

import android.content.Context
import com.wolop.wallet.data.local.AppDatabase
import com.wolop.wallet.data.repository.UsuarioRepository

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
