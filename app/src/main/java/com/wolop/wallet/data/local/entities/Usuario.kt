
package com.wolop.wallet.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,

    val correo: String,

    val telefono: String? = null,

    val fechaRegistro: Long = System.currentTimeMillis()
)
