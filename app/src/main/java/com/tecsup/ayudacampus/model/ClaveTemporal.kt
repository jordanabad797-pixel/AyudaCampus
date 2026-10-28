package com.tecsup.ayudacampus.model

import java.security.SecureRandom
import kotlin.random.Random
import kotlin.random.asKotlinRandom

/**
 * Contraseña temporal que Soporte genera al restablecer una cuenta.
 * 8 caracteres con al menos una letra y un número; sin caracteres confusos (0/O, 1/l/I).
 */
object ClaveTemporal {

    const val LONGITUD = 8

    private const val LETRAS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz"
    private const val NUMEROS = "23456789"
    private const val TODOS = LETRAS + NUMEROS

    fun generar(random: Random = SecureRandom().asKotlinRandom()): String {
        val caracteres = MutableList(LONGITUD - 2) { TODOS[random.nextInt(TODOS.length)] }
        caracteres += LETRAS[random.nextInt(LETRAS.length)]
        caracteres += NUMEROS[random.nextInt(NUMEROS.length)]
        caracteres.shuffle(random)
        return caracteres.joinToString("")
    }
}
