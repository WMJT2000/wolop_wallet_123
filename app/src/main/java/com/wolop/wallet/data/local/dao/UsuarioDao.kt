
package com.wolop.wallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.wolop.wallet.data.local.entities.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // Guardar un usuario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario): Long

    // Consultar todos los usuarios
    @Query("SELECT * FROM usuarios ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<Usuario>>

    // Buscar un usuario por su ID
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Usuario?

    // Buscar un usuario por su correo
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): Usuario?

    // Actualizar un usuario
    @Update
    suspend fun actualizar(usuario: Usuario)

    // Eliminar un usuario
    @Delete
    suspend fun eliminar(usuario: Usuario)

    // Eliminar todos los usuarios
    @Query("DELETE FROM usuarios")
    suspend fun eliminarTodos()
}
