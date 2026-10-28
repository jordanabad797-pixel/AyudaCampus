package com.tecsup.ayudacampus.model

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Las contraseñas nunca se guardan en texto: se guarda "sal:hash".
 *  - sal: 16 bytes aleatorios por usuario (dos claves iguales producen hashes distintos).
 *  - hash: SHA-256 aplicado [ITERACIONES] veces (hace lento un ataque de fuerza bruta).
 * La comparación es en tiempo constante (MessageDigest.isEqual).
 */
object HashClave {

    private const val ITERACIONES = 10_000
    private const val BYTES_SAL = 16
    private const val SEPARADOR = ':'

    fun generar(clave: String, sal: ByteArray = nuevaSal()): String =
        aHex(sal) + SEPARADOR + aHex(calcular(clave, sal))

    fun verificar(clave: String, guardado: String): Boolean {
        val partes = guardado.split(SEPARADOR)
        if (partes.size != 2) return false
        val sal = deHex(partes[0]) ?: return false
        val esperado = deHex(partes[1]) ?: return false
        return MessageDigest.isEqual(calcular(clave, sal), esperado)
    }

    private fun calcular(clave: String, sal: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        var resultado = sal + clave.toByteArray(Charsets.UTF_8)
        repeat(ITERACIONES) {
            digest.reset()
            digest.update(sal)
            resultado = digest.digest(resultado)
        }
        return resultado
    }

    private fun nuevaSal(): ByteArray = ByteArray(BYTES_SAL).also { SecureRandom().nextBytes(it) }

    private fun aHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }

    private fun deHex(texto: String): ByteArray? {
        if (texto.isEmpty() || texto.length % 2 != 0) return null
        return runCatching { ByteArray(texto.length / 2) { i -> texto.substring(i * 2, i * 2 + 2).toInt(16).toByte() } }
            .getOrNull()
    }
}
