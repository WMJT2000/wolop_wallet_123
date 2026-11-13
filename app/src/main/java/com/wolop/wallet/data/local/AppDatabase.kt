
package com.wolop.wallet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wolop.wallet.data.local.entities.Usuario
import com.wolop.wallet.data.local.dao.UsuarioDao
@Database(
    entities = [Usuario::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wolop_wallet.db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
