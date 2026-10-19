package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.AyudaButton
import com.tecsup.ayudacampus.ui.components.AyudaTextField
import com.tecsup.ayudacampus.ui.components.IconoCategoria
import com.tecsup.ayudacampus.ui.components.IconosCategoria
import com.tecsup.ayudacampus.ui.components.VarianteBoton
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel
import kotlinx.coroutines.launch

/** Sirve para crear (categoriaId = null) y para editar (categoriaId = id). */
@Composable
fun FormularioCategoriaScreen(
    vm: CategoriasViewModel,
    categoriaId: Int?,
    onAtras: () -> Unit
) {
    val categorias by vm.categorias.collectAsStateWithLifecycle()
    val errorNombre by vm.errorNombre.collectAsStateWithLifecycle()
    val procesando by vm.procesando.collectAsStateWithLifecycle()

    val original = categorias.firstOrNull { it.id == categoriaId }
    val esEdicion = categoriaId != null

    var nombre by rememberSaveable { mutableStateOf("") }
    var icono by rememberSaveable { mutableStateOf("tag") }
    var activa by rememberSaveable { mutableStateOf(true) }
    var cargado by rememberSaveable { mutableStateOf(!esEdicion) }
    var confirmarEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(original) {
        if (!cargado && original != null) {
            nombre = original.nombre
            icono = original.icono
            activa = original.activa
            cargado = true
        }
    }

    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        launch { vm.mensaje.collect { snackbar.showSnackbar(it) } }
        vm.listo.collect { onAtras() }
    }

    Scaffold(
        topBar = {
            AppTopBar(titulo = if (esEdicion) "Editar categoría" else "Nueva categoría", onAtras = onAtras)
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (!cargado) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AyudaTextField(
                valor = nombre,
                onValorChange = {
                    nombre = it
                    vm.limpiarErrorNombre()
                },
                etiqueta = "Nombre de la categoría",
                error = errorNombre,
                maxLength = 30,
                enabled = !procesando
            )

            Text("Ícono", style = MaterialTheme.typography.titleSmall)
            SelectorIcono(seleccionado = icono, onElegir = { icono = it })

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Categoría activa", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Los estudiantes podrán elegirla",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = activa, onCheckedChange = { activa = it })
                }
            }

            Text("Vista previa", style = MaterialTheme.typography.titleSmall)
            VistaPrevia(nombre = nombre, icono = icono, activa = activa)

            Spacer(Modifier.height(8.dp))
            AyudaButton(
                texto = "Guardar categoría",
                onClick = {
                    vm.guardar(
                        Categoria(
                            id = categoriaId ?: 0,
                            nombre = nombre,
                            icono = icono,
                            activa = activa
                        )
                    )
                },
                cargando = procesando
            )
            AyudaButton(texto = "Cancelar", onClick = onAtras, variante = VarianteBoton.CONTORNO)

            if (esEdicion && original != null) {
                TextButton(
                    onClick = { confirmarEliminar = true },
                    enabled = !procesando,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Eliminar categoría", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (confirmarEliminar && original != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar «${original.nombre}»?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    vm.eliminar(original)
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SelectorIcono(seleccionado: String, onElegir: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        IconosCategoria.claves.chunked(3).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                fila.forEach { clave ->
                    val activo = clave == seleccionado
                    Surface(
                        onClick = { onElegir(clave) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.6f),
                        shape = MaterialTheme.shapes.medium,
                        color = if (activo) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            if (activo) 2.dp else 1.dp,
                            if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                IconosCategoria.icono(clave),
                                contentDescription = "Ícono $clave",
                                tint = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VistaPrevia(nombre: String, icono: String, activa: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            IconoCategoria(clave = icono, tamano = 44.dp, activo = activa)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    nombre.ifBlank { "Nombre de la categoría" },
                    style = MaterialTheme.typography.titleSmall,
                    color = if (nombre.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    if (activa) "Visible para estudiantes" else "Oculta para estudiantes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
