package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.data.repository.AyudaRepository
import com.tecsup.ayudacampus.model.Prioridad
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NuevoTicketUiState(
    val categoriaId: Int? = null,
    val titulo: String = "",
    val ubicacion: String = "",
    val prioridad: Prioridad = Prioridad.MEDIA,
    val descripcion: String = "",
    val errorCategoria: String? = null,
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorGeneral: String? = null,
    val enviando: Boolean = false
)

class NuevoTicketViewModel(
    private val repo: AyudaRepository,
    private val clienteId: Int
) : ViewModel() {

    val categorias: StateFlow<List<Categoria>> = repo.observarCategoriasActivas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _ui = MutableStateFlow(NuevoTicketUiState())
    val ui: StateFlow<NuevoTicketUiState> = _ui.asStateFlow()

    private val _enviado = Channel<Unit>(Channel.BUFFERED)
    val enviado: Flow<Unit> = _enviado.receiveAsFlow()

    fun elegirCategoria(id: Int) = _ui.update { it.copy(categoriaId = id, errorCategoria = null) }

    fun cambiarTitulo(texto: String) = _ui.update { it.copy(titulo = texto, errorTitulo = null) }

    fun cambiarUbicacion(texto: String) = _ui.update { it.copy(ubicacion = texto) }

    fun cambiarPrioridad(p: Prioridad) = _ui.update { it.copy(prioridad = p) }

    fun cambiarDescripcion(texto: String) = _ui.update { it.copy(descripcion = texto, errorDescripcion = null) }

    fun enviar() {
        val estado = _ui.value
        if (estado.enviando) return

        val categoriaValida = estado.categoriaId != null && categorias.value.any { it.id == estado.categoriaId }
        val errorCategoria = if (categoriaValida) null else "Elige una categoría"
        val errorTitulo = if (estado.titulo.trim().length < MIN_TITULO) {
            "El título debe tener al menos $MIN_TITULO caracteres"
        } else null
        val errorDescripcion = if (estado.descripcion.trim().length < MIN_DESCRIPCION) {
            "La descripción debe tener al menos $MIN_DESCRIPCION caracteres"
        } else null

        if (errorCategoria != null || errorTitulo != null || errorDescripcion != null) {
            _ui.update {
                it.copy(
                    errorCategoria = errorCategoria,
                    errorTitulo = errorTitulo,
                    errorDescripcion = errorDescripcion
                )
            }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(enviando = true, errorGeneral = null) }
            try {
                repo.crearTicket(
                    clienteId = clienteId,
                    categoriaId = estado.categoriaId!!,
                    titulo = estado.titulo,
                    descripcion = estado.descripcion,
                    ubicacion = estado.ubicacion,
                    prioridad = estado.prioridad
                )
                _enviado.send(Unit)
            } catch (e: Exception) {
                _ui.update { it.copy(errorGeneral = "No se pudo enviar el ticket. Inténtalo de nuevo.") }
            } finally {
                _ui.update { it.copy(enviando = false) }
            }
        }
    }

    companion object {
        const val MIN_TITULO = 5
        const val MIN_DESCRIPCION = 10
    }
}
