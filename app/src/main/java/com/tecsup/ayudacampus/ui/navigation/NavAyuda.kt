package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.ayudacampus.AyudaApp
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel

/**
 * [GUIADO] Grafo de navegación de AyudaCampus.
 *
 * Proyecto base: Splash → Categorías.
 * TODO (Paso 6): Login y salto directo si ya hay sesión guardada.
 * TODO (Paso 12): grafo de cada perfil con su NavigationBar (itemsNavCliente / itemsNavSoporte).
 */
@Composable
fun NavAyuda() {
    val navController = rememberNavController()

    // TODO (Paso 6): el rol y el modo oscuro vendrán de SesionViewModel (DataStore).
    var rol by rememberSaveable { mutableStateOf(Rol.CLIENTE) }

    AyudaTheme(rol = rol) {
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable(Rutas.SPLASH) {
                val irACategorias = {
                    rol = Rol.ADMIN
                    navController.navigate(Rutas.CATEGORIAS) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                }
                SplashScreen(onComenzar = irACategorias, onTengoCuenta = irACategorias)
            }

            composable(Rutas.CATEGORIAS) {
                val vm = ayudaViewModel { app -> CategoriasViewModel(app.repository) }
                CategoriasScreen(
                    vm = vm,
                    onEditar = { /* TODO (Paso 11): navController.navigate(Rutas.formCategoria(it)) */ }
                )
            }
        }
    }
}

/**
 * Crea (o recupera) un ViewModel ligado a la pantalla actual, inyectándole
 * lo que necesite desde [AyudaApp] (el Repository, más adelante las preferencias).
 *
 *     val vm = ayudaViewModel { app -> CategoriasViewModel(app.repository) }
 */
@Composable
internal inline fun <reified VM : ViewModel> ayudaViewModel(
    noinline crear: (AyudaApp) -> VM
): VM {
    val app = LocalContext.current.applicationContext as AyudaApp
    return viewModel<VM>(factory =AyudaViewModelFactory { crear(app) })
}
