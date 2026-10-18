package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * [GUIADO] ViewModel de la gestión de categorías (Soporte TI).
 * Convierte el Flow de Room en un StateFlow que la pantalla observa con collectAsState().
 */
class CategoriasViewModel(private val repo: AyudaRepository) : ViewModel() {

    /** Lista viva: cuando la tabla cambia, la pantalla se actualiza sola. */
    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Activa o desactiva una categoría (las inactivas no se ofrecen al crear tickets). */
    fun alternar(c: Categoria) {
        viewModelScope.launch { repo.cambiarActiva(c.id, !c.activa) }
    }

    // TODO (Paso 11): guardar(categoria) y eliminar(categoria)
}
