package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.ayudacampus.AyudaApp
import com.tecsup.ayudacampus.data.local.entity.rolEnum
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.cliente.DetalleTicketScreen
import com.tecsup.ayudacampus.ui.cliente.LoginScreen
import com.tecsup.ayudacampus.ui.cliente.MisTicketsScreen
import com.tecsup.ayudacampus.ui.cliente.NuevoTicketScreen
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.soporte.BandejaScreen
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.ui.soporte.PanelScreen
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel
import com.tecsup.ayudacampus.viewmodel.NuevoTicketViewModel
import com.tecsup.ayudacampus.viewmodel.PanelViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel
import com.tecsup.ayudacampus.viewmodel.TicketDetalleViewModel

/**
 * [GUIADO] Grafo de navegación de AyudaCampus.
 * TODO (Paso 12): grafo de cada perfil con su NavigationBar (itemsNavCliente / itemsNavSoporte).
 */
@Composable
fun NavAyuda() {
    val navController = rememberNavController()

    val sesionVm = ayudaViewModel { app -> SesionViewModel(app.repository, app.preferencias) }
    val usuario by sesionVm.usuarioActual.collectAsStateWithLifecycle()
    val sesionVerificada by sesionVm.sesionVerificada.collectAsStateWithLifecycle()
    val modoOscuro by sesionVm.modoOscuro.collectAsStateWithLifecycle()
    val rolLogin by sesionVm.rolSeleccionado.collectAsStateWithLifecycle()

    // Con sesión: color del usuario. Sin sesión: color de la pestaña elegida en el Login.
    val rolTema = usuario?.rolEnum ?: rolLogin

    val cerrarSesion: () -> Unit = {
        sesionVm.cerrarSesion()
        navController.irALogin()
    }

    AyudaTheme(rol = rolTema, oscuro = modoOscuro) {
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable(Rutas.SPLASH) {
                LaunchedEffect(sesionVerificada, usuario) {
                    val u = usuario
                    if (sesionVerificada && u != null) navController.irAInicio(u.rolEnum)
                }
                SplashScreen(
                    onComenzar = { navController.irALogin() },
                    onTengoCuenta = { navController.irALogin() }
                )
            }

            composable(Rutas.LOGIN) {
                LaunchedEffect(Unit) {
                    sesionVm.ingresoExitoso.collect { rol -> navController.irAInicio(rol) }
                }
                LoginScreen(vm = sesionVm)
            }

            // ---------- Estudiante ----------
            composable(Rutas.MIS_TICKETS) {
                val u = usuario
                if (u == null) {
                    PantallaCargando()
                } else {
                    val vm = ayudaViewModel { app -> MisTicketsViewModel(app.repository, u.id) }
                    MisTicketsScreen(
                        vm = vm,
                        nombreUsuario = u.nombre,
                        onAbrirTicket = { navController.navigate(Rutas.detalleTicket(it)) },
                        onNuevoTicket = { navController.navigate(Rutas.NUEVO_TICKET) },
                        onCerrarSesion = cerrarSesion
                    )
                }
            }

            composable(Rutas.NUEVO_TICKET) {
                val u = usuario
                if (u == null) {
                    PantallaCargando()
                } else {
                    val vm = ayudaViewModel { app -> NuevoTicketViewModel(app.repository, u.id) }
                    NuevoTicketScreen(
                        vm = vm,
                        onAtras = { navController.popBackStack() },
                        onEnviado = { navController.popBackStack() }
                    )
                }
            }

            composable(
                route = Rutas.DETALLE_TICKET,
                arguments = listOf(navArgument(Rutas.ARG_TICKET_ID) { type = NavType.IntType })
            ) { entrada ->
                val ticketId = entrada.arguments?.getInt(Rutas.ARG_TICKET_ID) ?: 0
                val u = usuario
                if (u == null) {
                    PantallaCargando()
                } else {
                    val vm = ayudaViewModel { app -> TicketDetalleViewModel(app.repository, ticketId, u) }
                    DetalleTicketScreen(
                        vm = vm,
                        ticketId = ticketId,
                        onAtras = { navController.popBackStack() }
                    )
                }
            }

            // ---------- Soporte TI ----------
            composable(Rutas.PANEL) {
                val vm = ayudaViewModel { app -> PanelViewModel(app.repository) }
                PanelScreen(
                    vm = vm,
                    onVerBandeja = { navController.navigate(Rutas.BANDEJA) },
                    onVerCategorias = { navController.navigate(Rutas.CATEGORIAS) },
                    onCerrarSesion = cerrarSesion
                )
            }

            composable(Rutas.BANDEJA) {
                val vm = ayudaViewModel { app -> BandejaViewModel(app.repository) }
                BandejaScreen(
                    vm = vm,
                    onAtender = { /* TODO (Paso 11): navController.navigate(Rutas.atender(it)) */ },
                    onAtras = { navController.popBackStack() }
                )
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

/** Va a la pantalla principal del rol y limpia la pila (no se puede volver al login con "atrás"). */
private fun NavHostController.irAInicio(rol: Rol) {
    val destino = if (rol == Rol.CLIENTE) Rutas.MIS_TICKETS else Rutas.PANEL
    navigate(destino) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavHostController.irALogin() {
    navigate(Rutas.LOGIN) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
private fun PantallaCargando() {
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Crea (o recupera) un ViewModel ligado a la pantalla actual, inyectándole
 * lo que necesite desde [AyudaApp] (el Repository y las preferencias).
 */
@Composable
internal inline fun <reified VM : ViewModel> ayudaViewModel(
    noinline crear: (AyudaApp) -> VM
): VM {
    val app = LocalContext.current.applicationContext as AyudaApp
    return viewModel<VM>(factory = AyudaViewModelFactory { crear(app) })
}
