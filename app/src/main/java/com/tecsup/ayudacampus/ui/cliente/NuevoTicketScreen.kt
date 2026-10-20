package com.tecsup.ayudacampus.ui.cliente

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.model.Prioridad
import com.tecsup.ayudacampus.model.ReglasSla
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.AvisoMesaCerrada
import com.tecsup.ayudacampus.ui.components.IconosCategoria
import com.tecsup.ayudacampus.ui.theme.color
import com.tecsup.ayudacampus.viewmodel.NuevoTicketViewModel

@Composable
fun NuevoTicketScreen(
    vm: NuevoTicketViewModel,
    onAtras: () -> Unit,
    onEnviado: () -> Unit
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val categorias by vm.categorias.collectAsStateWithLifecycle()
    val mesaAbierta by vm.mesaAbierta.collectAsStateWithLifecycle()
    val foco = LocalFocusManager.current

    LaunchedEffect(Unit) {
        vm.enviado.collect { onEnviado() }
    }

    Scaffold(
        topBar = { AppTopBar(titulo = "Nuevo ticket", onAtras = onAtras) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (!mesaAbierta) {
                AvisoMesaCerrada(Modifier.padding(bottom = 8.dp))
            }
            Seccion("Categoría")
            SelectorCategoria(
                categorias = categorias,
                seleccionada = ui.categoriaId,
                onElegir = vm::elegirCategoria
            )
            ui.errorCategoria?.let { TextoError(it) }

            Spacer(Modifier.height(16.dp))
            AyudaTextField(
                valor = ui.titulo,
                onValorChange = vm::cambiarTitulo,
                etiqueta = "Título",
                placeholder = "Ej. Sin Wi-Fi en el laboratorio 3",
                error = ui.errorTitulo,
                maxLength = 80,
                enabled = !ui.enviando
            )
            AyudaTextField(
                valor = ui.ubicacion,
                onValorChange = vm::cambiarUbicacion,
                etiqueta = "Ubicación (opcional)",
                icono = Icons.Outlined.Place,
                placeholder = "Ej. Lab 3 - Pabellón B",
                maxLength = 60,
                enabled = !ui.enviando
            )

            Seccion("Prioridad")
            SelectorPrioridad(seleccionada = ui.prioridad, enabled = !ui.enviando, onElegir = vm::cambiarPrioridad)

            Spacer(Modifier.height(16.dp))
            AyudaTextField(
                valor = ui.descripcion,
                onValorChange = vm::cambiarDescripcion,
                etiqueta = "Descripción",
                placeholder = "Cuéntanos qué pasa, desde cuándo y a cuántas personas afecta",
                error = ui.errorDescripcion,
                singleLine = false,
                minLines = 4,
                maxLength = 500,
                imeAction = ImeAction.Default,
                enabled = !ui.enviando
            )

            TiempoEstimado(ui.prioridad)

            ui.errorGeneral?.let { TextoError(it) }

            Spacer(Modifier.height(20.dp))
            AyudaButton(
                texto = "Enviar ticket",
                onClick = {
                    foco.clearFocus()
                    vm.enviar()
                },
                cargando = ui.enviando,
                enabled = mesaAbierta
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Seccion(titulo: String) {
    Text(
        titulo,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    )
}

@Composable
private fun TextoError(texto: String) {
    Text(
        texto,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SelectorCategoria(
    categorias: List<Categoria>,
    seleccionada: Int?,
    onElegir: (Int) -> Unit
) {
    if (categorias.isEmpty()) {
        Text(
            "No hay categorías disponibles por ahora.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        categorias.forEach { c ->
            val activa = c.id == seleccionada
            FilterChip(
                selected = activa,
                onClick = { onElegir(c.id) },
                label = { Text(c.nombre) },
                leadingIcon = {
                    Icon(IconosCategoria.icono(c.icono), contentDescription = null, modifier = Modifier.size(18.dp))
                },
                shape = MaterialTheme.shapes.small,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = activa,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun SelectorPrioridad(seleccionada: Prioridad, enabled: Boolean, onElegir: (Prioridad) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Prioridad.entries.forEach { p ->
            val activa = p == seleccionada
            val fondo by animateColorAsState(
                if (activa) p.color() else MaterialTheme.colorScheme.surface,
                label = "fondoPrioridad"
            )
            Surface(
                onClick = { onElegir(p) },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp),
                shape = MaterialTheme.shapes.small,
                color = fondo,
                contentColor = if (activa) Color.White else MaterialTheme.colorScheme.onSurface,
                border = if (activa) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Text(
                    p.etiqueta,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun TiempoEstimado(prioridad: Prioridad) {
    val horas = ReglasSla.plazoHoras(prioridad)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Tiempo de respuesta estimado: $horas horas",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
