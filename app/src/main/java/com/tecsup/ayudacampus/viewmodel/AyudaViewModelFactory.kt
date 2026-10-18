package com.tecsup.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * [ENTREGADO] Fábrica genérica de ViewModels.
 *
 * Android crea los ViewModels (para que sobrevivan a la rotación), pero los nuestros
 * necesitan parámetros (el Repository, el id del ticket, el usuario...). Esta fábrica
 * recibe una función que sabe construir el ViewModel, así sirve para TODOS sin modificarla:
 *
 *     AyudaViewModelFactory { CategoriasViewModel(app.repository) }
 */
class AyudaViewModelFactory<VM : ViewModel>(
    private val crear: () -> VM
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm = crear()
        require(modelClass.isAssignableFrom(vm.javaClass)) {
            "La fábrica creó ${vm.javaClass.simpleName} pero se pidió ${modelClass.simpleName}"
        }
        return vm as T
    }
}
