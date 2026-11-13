
package com.wolop.wallet.data.repository

import com.wolop.wallet.data.local.dao.UsuarioDao
import com.wolop.wallet.data.local.entities.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    // Guardar un usuario
    suspend fun insertarUsuario(usuario: Usuario): Long {
        return usuarioDao.insertar(usuario)
    }

    // Obtener todos los usuarios
    fun obtenerUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodos()
    }

    // Buscar usuario por ID
    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return usuarioDao.obtenerPorId(id)
    }

    // Buscar usuario por correo
    suspend fun obtenerUsuarioPorCorreo(correo: String): Usuario? {
        return usuarioDao.obtenerPorCorreo(correo)
    }

    // Actualizar usuario
    suspend fun actualizarUsuario(usuario: Usuario) {
        usuarioDao.actualizar(usuario)
    }

    // Eliminar usuario
    suspend fun eliminarUsuario(usuario: Usuario) {
        usuarioDao.eliminar(usuario)
    }

    // Eliminar todos los usuarios
    suspend fun eliminarTodosLosUsuarios() {
        usuarioDao.eliminarTodos()
    }
}
