package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.FilaAccion
import com.tecsup.ayudacampus.ui.components.FilaPreferencia
import com.tecsup.ayudacampus.ui.components.GrupoPreferencias
import com.tecsup.ayudacampus.ui.components.NotaDataStore
import com.tecsup.ayudacampus.ui.components.TarjetaCambiarPerfil
import com.tecsup.ayudacampus.ui.components.TarjetaUsuario
import com.tecsup.ayudacampus.ui.components.VarianteBoton
import com.tecsup.ayudacampus.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(
    usuario: Usuario,
    sesionVm: SesionViewModel,
    onCambiarClave: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val modoOscuro by sesionVm.modoOscuro.collectAsStateWithLifecycle()
    val notificaciones by sesionVm.notificaciones.collectAsStateWithLifecycle()
    val recibirTickets by sesionVm.recibirTickets.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(titulo = "Ajustes") },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TarjetaUsuario(nombre = usuario.nombre, correo = usuario.correo, etiqueta = "Perfil activo")

            TarjetaCambiarPerfil(destino = "Estudiante", onClick = sesionVm::cambiarPerfil)

            Text("Seguridad", style = MaterialTheme.typography.titleSmall)
            GrupoPreferencias {
                FilaAccion(
                    titulo = "Cambiar contraseña",
                    detalle = "Actualiza la contraseña de tu cuenta",
                    icono = Icons.Outlined.Key,
                    onClick = onCambiarClave
                )
            }

            Text("Preferencias", style = MaterialTheme.typography.titleSmall)
            GrupoPreferencias {
                FilaPreferencia(
                    titulo = "Modo oscuro",
                    detalle = "Cambia el tema de la app",
                    activo = modoOscuro,
                    onCambio = sesionVm::cambiarModoOscuro
                )
                FilaPreferencia(
                    titulo = "Notificaciones",
                    detalle = "Avisar tickets nuevos",
                    activo = notificaciones,
                    onCambio = sesionVm::cambiarNotificaciones
                )
                FilaPreferencia(
                    titulo = "Recibir tickets",
                    detalle = "Abrir o cerrar la mesa de ayuda",
                    activo = recibirTickets,
                    onCambio = sesionVm::cambiarRecibirTickets,
                    mostrarSeparador = false
                )
            }
            NotaDataStore()

            AyudaButton(texto = "Cerrar sesión", onClick = onCerrarSesion, variante = VarianteBoton.PELIGRO)
        }
    }
}
