// Archivo de build del proyecto (raíz).
// Las versiones se fijan aquí; el módulo :app solo aplica los plugins.
// IMPORTANTE: la versión de KSP debe empezar con la versión de Kotlin (2.0.21-…).
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}
