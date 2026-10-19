package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [GUIADO] ViewModel de la gestión de categorías (Soporte TI).
 * Convierte el Flow de Room en un StateFlow que la pantalla observa con collectAsState().
 */
class CategoriasViewModel(private val repo: AyudaRepository) : ViewModel() {

    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _errorNombre = MutableStateFlow<String?>(null)
    val errorNombre: StateFlow<String?> = _errorNombre.asStateFlow()

    private val _procesando = MutableStateFlow(false)
    val procesando: StateFlow<Boolean> = _procesando.asStateFlow()

    private val _mensaje = Channel<String>(Channel.BUFFERED)
    val mensaje: Flow<String> = _mensaje.receiveAsFlow()

    private val _listo = Channel<Unit>(Channel.BUFFERED)
    /** Se emite al guardar o eliminar con éxito (el formulario se cierra). */
    val listo: Flow<Unit> = _listo.receiveAsFlow()

    fun alternar(c: Categoria) {
        viewModelScope.launch { repo.cambiarActiva(c.id, !c.activa) }
    }

    fun limpiarErrorNombre() {
        _errorNombre.value = null
    }

    /** id == 0 → nueva; id > 0 → edición. */
    fun guardar(categoria: Categoria) {
        val nombre = categoria.nombre.trim()
        val repetido = categorias.value.any { it.id != categoria.id && it.nombre.equals(nombre, ignoreCase = true) }
        _errorNombre.value = when {
            nombre.isEmpty() -> "El nombre es obligatorio"
            nombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            repetido -> "Ya existe una categoría con ese nombre"
            else -> null
        }
        if (_errorNombre.value != null) return

        ejecutar {
            repo.guardarCategoria(categoria.copy(nombre = nombre))
            _listo.send(Unit)
        }
    }

    /** Regla: no se puede eliminar una categoría que ya tiene tickets. */
    fun eliminar(categoria: Categoria) {
        ejecutar {
            val tickets = repo.contarTicketsDeCategoria(categoria.id)
            if (tickets > 0) {
                val texto = if (tickets == 1) "1 ticket" else "$tickets tickets"
                _mensaje.send("No se puede eliminar «${categoria.nombre}»: tiene $texto. Puedes desactivarla.")
            } else {
                repo.eliminarCategoria(categoria)
                _listo.send(Unit)
            }
        }
    }

    private fun ejecutar(accion: suspend () -> Unit) {
        if (_procesando.value) return
        viewModelScope.launch {
            _procesando.value = true
            try {
                accion()
            } catch (e: Exception) {
                _mensaje.send("No se pudo completar la acción. Inténtalo de nuevo.")
            } finally {
                _procesando.value = false
            }
        }
    }
}
