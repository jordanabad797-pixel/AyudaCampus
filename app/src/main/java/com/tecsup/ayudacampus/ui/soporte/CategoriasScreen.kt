package com.tecsup.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.ayudacampus.data.local.entity.Categoria
import com.tecsup.ayudacampus.ui.components.AppTopBar
import com.tecsup.ayudacampus.ui.components.EstadoVacio
import com.tecsup.ayudacampus.ui.components.IconoCategoria
import com.tecsup.ayudacampus.ui.components.Ilustracion
import com.tecsup.ayudacampus.viewmodel.CategoriasViewModel

/**
 * [ENTREGADO] Ejemplo resuelto de pantalla: la View SOLO observa el estado y avisa eventos.
 * No conoce el DAO ni el Repository; todo pasa por [CategoriasViewModel].
 *
 * @param onEditar abre el formulario para editar (Paso 11).
 * @param onNueva abre el formulario para crear; si es null no se muestra el botón +.
 */
@Composable
fun CategoriasScreen(
    vm: CategoriasViewModel,
    onEditar: (Int) -> Unit,
    onNueva: (() -> Unit)? = null
) {
    val categorias by vm.categorias.collectAsStateWithLifecycle()
    val activas = categorias.count { it.activa }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Categorías",
                subtitulo = "$activas activas de ${categorias.size}"
            )
        },
        floatingActionButton = {
            if (onNueva != null) {
                FloatingActionButton(
                    onClick = onNueva,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Nueva categoría")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (categorias.isEmpty()) {
            EstadoVacio(
                icono = Icons.Outlined.LocalOffer,
                titulo = "Aún no hay categorías",
                detalle = "Crea la primera con el botón +",
                ilustracion = Ilustracion.CATEGORIAS,
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categorias, key = { it.id }) { c ->
                    CategoriaItem(
                        c = c,
                        onToggle = { vm.alternar(c) },
                        onEditar = { onEditar(c.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoriaItem(
    c: Categoria,
    onToggle: () -> Unit,
    onEditar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, top = 12.dp, end = 4.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoCategoria(clave = c.icono, tamano = 44.dp, activo = c.activa)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = c.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (c.activa) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (c.activa) "Visible para estudiantes" else "Oculta para estudiantes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = c.activa,
                onCheckedChange = { onToggle() },
                modifier = Modifier.semantics {
                    contentDescription = if (c.activa) "Desactivar ${c.nombre}" else "Activar ${c.nombre}"
                }
            )
            IconButton(onClick = onEditar) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Editar ${c.nombre}",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
