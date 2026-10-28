package com.tecsup.ayudacampus.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tecsup.ayudacampus.model.Rol

/**
 * [TÚ LO HACES · Paso 1] Tabla "usuarios".
 *
 * Un mismo dispositivo guarda los dos perfiles (modo práctica):
 *  - Estudiante → rol = "CLIENTE"
 *  - Soporte TI → rol = "ADMIN"
 *
 * El rol se guarda como texto (Rol.name) porque SQLite no conoce los enum de Kotlin.
 * El índice único en [correo] evita dos cuentas con el mismo correo.
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val clave: String, // Solo datos ficticios de práctica (Ley 29733). En producción se guardaría un hash.
    val rol: String = Rol.CLIENTE.name,
    /** true → tiene una contraseña temporal y debe cambiarla antes de entrar. */
    val debeCambiarClave: Boolean = false
)

/** Rol del usuario como enum (lectura segura del texto guardado). */
val Usuario.rolEnum: Rol
    get() = Rol.desde(rol) ?: Rol.CLIENTE
