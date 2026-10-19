package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.Usuario
import com.tecsup.ayudacampus.data.local.entity.rolEnum
import com.tecsup.ayudacampus.model.EstadoTicket
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.FilaPreferencia
import com.tecsup.ayudacampus.ui.components.GrupoPreferencias
import com.tecsup.ayudacampus.ui.components.NotaDataStore
import com.tecsup.ayudacampus.ui.components.TarjetaCambiarPerfil
import com.tecsup.ayudacampus.ui.components.TarjetaUsuario
import com.tecsup.ayudacampus.ui.components.VarianteBoton
import com.tecsup.ayudacampus.ui.theme.color
import com.tecsup.ayudacampus.viewmodel.MisTicketsViewModel
import com.tecsup.ayudacampus.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(
    usuario: Usuario,
    ticketsVm: MisTicketsViewModel,
    sesionVm: SesionViewModel,
    onCerrarSesion: () -> Unit
) {
    val abiertos by ticketsVm.abiertos.collectAsStateWithLifecycle()
    val resueltos by ticketsVm.resueltos.collectAsStateWithLifecycle()
    val total by ticketsVm.total.collectAsStateWithLifecycle()
    val modoOscuro by sesionVm.modoOscuro.collectAsStateWithLifecycle()
    val notificaciones by sesionVm.notificaciones.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(titulo = "Perfil") },
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
            TarjetaUsuario(
                nombre = usuario.nombre,
                correo = usuario.correo,
                etiqueta = usuario.rolEnum.etiqueta
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador(abiertos, "Abiertos", EstadoTicket.ABIERTO.color(), Modifier.weight(1f))
                Contador(resueltos, "Resueltos", EstadoTicket.RESUELTO.color(), Modifier.weight(1f))
                Contador(total, "Total", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            }

            TarjetaCambiarPerfil(destino = "Soporte TI", onClick = sesionVm::cambiarPerfil)

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
                    detalle = "Avisar cambios de estado",
                    activo = notificaciones,
                    onCambio = sesionVm::cambiarNotificaciones,
                    mostrarSeparador = false
                )
            }
            NotaDataStore()

            AyudaButton(texto = "Cerrar sesión", onClick = onCerrarSesion, variante = VarianteBoton.PELIGRO)
        }
    }
}

@Composable
private fun Contador(valor: Int, etiqueta: String, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = color.copy(alpha = 0.14f), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor.toString(), style = MaterialTheme.typography.headlineSmall, color = color)
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}
