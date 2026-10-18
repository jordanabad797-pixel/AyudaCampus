package com.tecsup.ayudacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.ayudacampus.ui.navigation.NavAyuda

/**
 * [ENTREGADO] Única Activity de la app.
 * Solo dibuja el grafo de navegación; el tema (según rol y modo oscuro) lo aplica NavAyuda.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavAyuda()
        }
    }
}
