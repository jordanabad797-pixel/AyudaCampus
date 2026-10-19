package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.ayudacampus.AyudaApp
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.entity.rolEnum
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.cliente.LoginScreen
import com.tecsup.ayudacampus.ui.cliente.MisTicketsScreen
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.VarianteBoton
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel

/**
 * [GUIADO] Grafo de navegación de AyudaCampus.
 *
 * Paso 6: Splash → Login → pantalla principal según el rol.
 *  - Si ya hay sesión guardada, el Splash salta el login.
 *  - El tema (cian / violeta y modo oscuro) depende de la sesión.
 * Paso 7: Mis tickets del estudiante.
 * TODO (Paso 10): Panel reemplaza a la pantalla pendiente de soporte.
 * TODO (Paso 12): grafo de cada perfil con su NavigationBar (itemsNavCliente / itemsNavSoporte).
 */
@Composable
fun NavAyuda() {
    val navController = rememberNavController()

    // ViewModel de sesión a nivel de Activity: lo comparten todas las pantallas.
    val sesionVm = ayudaViewModel { app -> SesionViewModel(app.repository, app.preferencias) }
    val usuario by sesionVm.usuarioActual.collectAsStateWithLifecycle()
    val sesionVerificada by sesionVm.sesionVerificada.collectAsStateWithLifecycle()
    val modoOscuro by sesionVm.modoOscuro.collectAsStateWithLifecycle()
    val rolLogin by sesionVm.rolSeleccionado.collectAsStateWithLifecycle()

    // Con sesión: color del usuario. Sin sesión: color de la pestaña elegida en el Login.
    val rolTema = usuario?.rolEnum ?: rolLogin

    AyudaTheme(rol = rolTema, oscuro = modoOscuro) {
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable(Rutas.SPLASH) {
                // Si ya hay perfil guardado, salta el login.
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
                // El ViewModel avisa (una sola vez) cuando el login fue exitoso → navegamos.
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
                        onAbrirTicket = { /* TODO (Paso 9): navController.navigate(Rutas.detalleTicket(it)) */ },
                        onNuevoTicket = { /* TODO (Paso 8): navController.navigate(Rutas.NUEVO_TICKET) */ },
                        // Temporal: en el Paso 12 se cierra sesión desde Perfil.
                        onCerrarSesion = {
                            sesionVm.cerrarSesion()
                            navController.irALogin()
                        }
                    )
                }
            }

            // ---------- Soporte TI ----------
            composable(Rutas.PANEL) {
                // TODO (Paso 10): PanelScreen
                PantallaPendiente(
                    titulo = "Panel de soporte",
                    usuario = usuario,
                    detalle = "Esta pantalla se construye en el Paso 10.",
                    onCerrarSesion = {
                        sesionVm.cerrarSesion()
                        navController.irALogin()
                    },
                    onVerCategorias = { navController.navigate(Rutas.CATEGORIAS) }
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

/** Va al Login y limpia la pila. */
private fun NavHostController.irALogin() {
    navigate(Rutas.LOGIN) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

/** Mientras se recupera el usuario de la sesión. */
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

/** Pantalla temporal mientras se construye el Panel de soporte (Paso 10). */
@Composable
private fun PantallaPendiente(
    titulo: String,
    usuario: Usuario?,
    detalle: String,
    onCerrarSesion: () -> Unit,
    onVerCategorias: (() -> Unit)? = null
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Outlined.Construction,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(titulo, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                "Hola, ${usuario?.nombre ?: ""} (${usuario?.rolEnum?.etiqueta ?: ""})",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                detalle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            if (onVerCategorias != null) {
                AyudaButton(texto = "Ver categorías", onClick = onVerCategorias)
                Spacer(Modifier.height(12.dp))
            }
            AyudaButton(texto = "Cerrar sesión", onClick = onCerrarSesion, variante = VarianteBoton.PELIGRO)
        }
    }
}

/**
 * Crea (o recupera) un ViewModel ligado a la pantalla actual, inyectándole
 * lo que necesite desde [AyudaApp] (el Repository y las preferencias).
 *
 *     val vm = ayudaViewModel { app -> CategoriasViewModel(app.repository) }
 */
@Composable
internal inline fun <reified VM : ViewModel> ayudaViewModel(
    noinline crear: (AyudaApp) -> VM
): VM {
    val app = LocalContext.current.applicationContext as AyudaApp
    return viewModel<VM>(factory = AyudaViewModelFactory { crear(app) })
}
