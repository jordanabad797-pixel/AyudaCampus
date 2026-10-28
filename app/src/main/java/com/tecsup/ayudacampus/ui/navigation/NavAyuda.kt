package com.tecsup.ayudacampus.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.ayudacampus.AyudaApp
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.entity.rolEnum
import com.tecsup.ayudacampus.model.Rol
import com.tecsup.ayudacampus.ui.cliente.CambiarClaveScreen
import com.tecsup.ayudacampus.ui.cliente.DetalleTicketScreen
import com.tecsup.ayudacampus.ui.cliente.LoginScreen
import com.tecsup.ayudacampus.ui.cliente.MisTicketsScreen
import com.tecsup.ayudacampus.ui.cliente.NuevoTicketScreen
import com.tecsup.ayudacampus.ui.cliente.PerfilScreen
import com.tecsup.ayudacampus.ui.cliente.RecuperarClaveScreen
import com.tecsup.ayudacampus.ui.cliente.RegistroScreen
import com.tecsup.ayudacampus.ui.cliente.SplashScreen
import com.tecsup.ayudacampus.ui.soporte.AjustesScreen
import com.tecsup.ayudacampus.ui.soporte.AtenderTicketScreen
import com.tecsup.ayudacampus.ui.soporte.BandejaScreen
import com.tecsup.ayudacampus.ui.soporte.CategoriasScreen
import com.tecsup.ayudacampus.ui.soporte.FormularioCategoriaScreen
import com.tecsup.ayudacampus.ui.soporte.PanelScreen
import com.tecsup.ayudacampus.ui.theme.AyudaTheme
import com.tecsup.ayudacampus.viewmodel.AtenderViewModel
import com.tecsup.ayudacampus.viewmodel.AyudaViewModelFactory
import com.tecsup.ayudacampus.viewmodel.BandejaViewModel
import com.tecsup.ayudacampus.viewmodel.CambiarClaveViewModel
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel
import com.tecsup.ayudacampus.viewmodel.NuevoTicketViewModel
import com.tecsup.ayudacampus.viewmodel.PanelViewModel
import com.tecsup.ayudacampus.viewmodel.RecuperarClaveViewModel
import com.tecsup.ayudacampus.viewmodel.RegistroViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel
import com.tecsup.ayudacampus.viewmodel.TicketDetalleViewModel

/**
 * [GUIADO] Navegación de AyudaCampus: Splash → Login → grafo del perfil.
 * Cada perfil tiene su propio grafo y su NavigationBar (3 ítems estudiante, 4 soporte).
 */
@Composable
fun NavAyuda() {
    val navController = rememberNavController()

    val sesionVm = ayudaViewModel { app -> SesionViewModel(app.repository, app.preferencias) }
    val usuario by sesionVm.usuarioActual.collectAsStateWithLifecycle()
    val sesionVerificada by sesionVm.sesionVerificada.collectAsStateWithLifecycle()
    val modoOscuro by sesionVm.modoOscuro.collectAsStateWithLifecycle()
    val rolLogin by sesionVm.rolSeleccionado.collectAsStateWithLifecycle()
    val recibirTickets by sesionVm.recibirTickets.collectAsStateWithLifecycle()

    val rolTema = usuario?.rolEnum ?: rolLogin

    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val itemsBarra = when {
        itemsNavCliente.any { it.ruta == rutaActual } -> itemsNavCliente
        itemsNavSoporte.any { it.ruta == rutaActual } -> itemsNavSoporte
        else -> emptyList()
    }

    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        sesionVm.irAInicio.collect { rol -> navController.irAInicio(rol) }
    }
    LaunchedEffect(Unit) {
        sesionVm.aviso.collect { snackbar.showSnackbar(it) }
    }
    LaunchedEffect(Unit) {
        sesionVm.cambiarClaveObligatorio.collect { id -> navController.navigate(Rutas.cambiarClave(id, obligatorio = true)) }
    }

    val cerrarSesion: () -> Unit = {
        sesionVm.cerrarSesion()
        navController.irALogin()
    }

    AyudaTheme(rol = rolTema, oscuro = modoOscuro) {
        Scaffold(
            bottomBar = {
                if (itemsBarra.isNotEmpty()) {
                    BarraNavegacion(items = itemsBarra, rutaActual = rutaActual) { ruta ->
                        navController.irAPestana(ruta, inicio = itemsBarra.first().ruta)
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbar) },
            contentWindowInsets = WindowInsets(0),
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Rutas.SPLASH,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding),
                enterTransition = {
                    fadeIn(tween(DURACION_MS)) + slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(DURACION_MS)
                    ) { it / 8 }
                },
                exitTransition = { fadeOut(tween(DURACION_MS / 2)) },
                popEnterTransition = { fadeIn(tween(DURACION_MS)) },
                popExitTransition = {
                    fadeOut(tween(DURACION_MS / 2)) + slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(DURACION_MS)
                    ) { it / 8 }
                }
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
                    LoginScreen(
                        vm = sesionVm,
                        onRegistrarse = { navController.navigate(Rutas.REGISTRO) },
                        onOlvideClave = { navController.navigate(Rutas.RECUPERAR_CLAVE) }
                    )
                }

                composable(Rutas.RECUPERAR_CLAVE) {
                    val vm = ayudaViewModel { app -> RecuperarClaveViewModel(app.repository) }
                    RecuperarClaveScreen(vm = vm, onAtras = { navController.popBackStack() })
                }

                composable(
                    route = Rutas.CAMBIAR_CLAVE,
                    arguments = listOf(
                        navArgument(Rutas.ARG_USUARIO_ID) { type = NavType.IntType },
                        navArgument(Rutas.ARG_OBLIGATORIO) {
                            type = NavType.BoolType
                            defaultValue = false
                        }
                    )
                ) { entradaClave ->
                    val usuarioId = entradaClave.arguments?.getInt(Rutas.ARG_USUARIO_ID) ?: 0
                    val obligatorio = entradaClave.arguments?.getBoolean(Rutas.ARG_OBLIGATORIO) ?: false
                    val vm = ayudaViewModel { app ->
                        CambiarClaveViewModel(app.repository, app.preferencias, usuarioId, obligatorio)
                    }
                    LaunchedEffect(Unit) {
                        vm.listo.collect { rol ->
                            if (obligatorio) {
                                navController.irAInicio(rol)
                            } else {
                                sesionVm.avisar("Contraseña actualizada")
                                navController.popBackStack()
                            }
                        }
                    }
                    CambiarClaveScreen(vm = vm, onAtras = { navController.popBackStack() })
                }

                composable(Rutas.REGISTRO) {
                    val vm = ayudaViewModel { app -> RegistroViewModel(app.repository, app.preferencias) }
                    LaunchedEffect(Unit) {
                        vm.registrado.collect { navController.irAInicio(Rol.CLIENTE) }
                    }
                    RegistroScreen(vm = vm, onAtras = { navController.popBackStack() })
                }

                navigation(startDestination = Rutas.MIS_TICKETS, route = Rutas.GRAFO_CLIENTE) {
                    composable(Rutas.MIS_TICKETS) {
                        ConUsuario(usuario, Rol.CLIENTE) { u ->
                            val vm = ayudaViewModel { app -> MisTicketsViewModel(app.repository, u.id) }
                            MisTicketsScreen(
                                vm = vm,
                                nombreUsuario = u.nombre,
                                onAbrirTicket = { navController.navigate(Rutas.detalleTicket(it)) },
                                onNuevoTicket = { navController.irAPestana(Rutas.NUEVO_TICKET, Rutas.MIS_TICKETS) },
                                mesaAbierta = recibirTickets
                            )
                        }
                    }

                    composable(Rutas.NUEVO_TICKET) {
                        ConUsuario(usuario, Rol.CLIENTE) { u ->
                            val vm = ayudaViewModel { app ->
                                NuevoTicketViewModel(app.repository, u.id, app.preferencias.recibirTickets)
                            }
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
                    ) { entradaTicket ->
                        val ticketId = entradaTicket.arguments?.getInt(Rutas.ARG_TICKET_ID) ?: 0
                        ConUsuario(usuario, Rol.CLIENTE) { u ->
                            val vm = ayudaViewModel { app -> TicketDetalleViewModel(app.repository, ticketId, u) }
                            DetalleTicketScreen(vm = vm, ticketId = ticketId, onAtras = { navController.popBackStack() })
                        }
                    }

                    composable(Rutas.PERFIL) {
                        ConUsuario(usuario, Rol.CLIENTE) { u ->
                            val ticketsVm = ayudaViewModel { app -> MisTicketsViewModel(app.repository, u.id) }
                            PerfilScreen(
                                usuario = u,
                                ticketsVm = ticketsVm,
                                sesionVm = sesionVm,
                                onCambiarClave = { navController.navigate(Rutas.cambiarClave(u.id)) },
                                onCerrarSesion = cerrarSesion
                            )
                        }
                    }
                }

                navigation(startDestination = Rutas.PANEL, route = Rutas.GRAFO_SOPORTE) {
                    composable(Rutas.PANEL) {
                        val vm = ayudaViewModel { app -> PanelViewModel(app.repository) }
                        PanelScreen(
                            vm = vm,
                            onVerBandeja = { navController.irAPestana(Rutas.BANDEJA, Rutas.PANEL) }
                        )
                    }

                    composable(Rutas.BANDEJA) {
                        val vm = ayudaViewModel { app -> BandejaViewModel(app.repository) }
                        BandejaScreen(vm = vm, onAtender = { navController.navigate(Rutas.atender(it)) })
                    }

                    composable(
                        route = Rutas.ATENDER,
                        arguments = listOf(navArgument(Rutas.ARG_TICKET_ID) { type = NavType.IntType })
                    ) { entradaTicket ->
                        val ticketId = entradaTicket.arguments?.getInt(Rutas.ARG_TICKET_ID) ?: 0
                        ConUsuario(usuario, Rol.ADMIN) { u ->
                            val vm = ayudaViewModel { app -> AtenderViewModel(app.repository, ticketId, u) }
                            AtenderTicketScreen(vm = vm, ticketId = ticketId, onAtras = { navController.popBackStack() })
                        }
                    }

                    composable(Rutas.CATEGORIAS) {
                        val vm = ayudaViewModel { app -> CategoriasViewModel(app.repository) }
                        CategoriasScreen(
                            vm = vm,
                            onEditar = { navController.navigate(Rutas.formCategoria(it)) },
                            onNueva = { navController.navigate(Rutas.formCategoria()) }
                        )
                    }

                    composable(
                        route = Rutas.FORM_CATEGORIA,
                        arguments = listOf(
                            navArgument(Rutas.ARG_CATEGORIA_ID) {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) { entradaForm ->
                        val categoriaId = entradaForm.arguments?.getInt(Rutas.ARG_CATEGORIA_ID)?.takeIf { it > 0 }
                        val vm = ayudaViewModel { app -> CategoriasViewModel(app.repository) }
                        FormularioCategoriaScreen(
                            vm = vm,
                            categoriaId = categoriaId,
                            onAtras = { navController.popBackStack() }
                        )
                    }

                    composable(Rutas.AJUSTES) {
                        ConUsuario(usuario, Rol.ADMIN) { u ->
                            AjustesScreen(
                                usuario = u,
                                sesionVm = sesionVm,
                                onCambiarClave = { navController.navigate(Rutas.cambiarClave(u.id)) },
                                onCerrarSesion = cerrarSesion
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraNavegacion(items: List<ItemNav>, rutaActual: String?, onElegir: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.ruta == rutaActual,
                onClick = { if (item.ruta != rutaActual) onElegir(item.ruta) },
                icon = { Icon(item.icono, contentDescription = null) },
                label = { Text(item.etiqueta) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Muestra el contenido solo cuando el usuario de la sesión ya cargó y tiene el rol esperado.
 * Evita crear un ViewModel con el usuario anterior justo después de cambiar de perfil.
 */
@Composable
private fun ConUsuario(usuario: Usuario?, rol: Rol, contenido: @Composable (Usuario) -> Unit) {
    if (usuario != null && usuario.rolEnum == rol) contenido(usuario) else PantallaCargando()
}

/** Duración de las transiciones entre pantallas (suaves: solo 1/8 del ancho + fundido). */
private const val DURACION_MS = 280

/** Va al grafo del rol y limpia toda la pila (no se puede volver con "atrás"). */
private fun NavHostController.irAInicio(rol: Rol) {
    val grafo = if (rol == Rol.CLIENTE) Rutas.GRAFO_CLIENTE else Rutas.GRAFO_SOPORTE
    navigate(grafo) {
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

/** Cambio de pestaña: conserva el estado de cada pestaña y no apila pantallas repetidas. */
private fun NavHostController.irAPestana(ruta: String, inicio: String) {
    navigate(ruta) {
        popUpTo(inicio) { saveState = true }
        launchSingleTop = true
        restoreState = true
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

/** Crea (o recupera) un ViewModel de la pantalla actual con lo que necesite de [AyudaApp]. */
@Composable
internal inline fun <reified VM : ViewModel> ayudaViewModel(
    noinline crear: (AyudaApp) -> VM
): VM {
    val app = LocalContext.current.applicationContext as AyudaApp
    return viewModel<VM>(factory = AyudaViewModelFactory { crear(app) })
}
