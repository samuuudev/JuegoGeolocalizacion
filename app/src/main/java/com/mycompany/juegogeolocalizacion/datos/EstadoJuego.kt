package com.mycompany.juegogeolocalizacion.datos

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

object EstadoJuego {
    var nombreJugador = mutableStateOf("")

    val resultadoSitio = mutableStateMapOf<Int, Boolean>()

    val seleccionJugador = mutableMapOf<Int, Pair<Double, Double>>()

    private val historialSitios = mutableMapOf<Int, MutableList<ResultadoIntento>>()

    fun juegoCompletado(totalSitios: Int): Boolean {
        return resultadoSitio.size == totalSitios
    }

    fun registrarIntento(
        sitioId: Int,
        latitud: Double,
        longitud: Double,
        distanciaKm: Double,
        acierto: Boolean
    ) {
        val lista = historialSitios.getOrPut(sitioId) { mutableListOf() }
        lista.add(ResultadoIntento(latitud, longitud, distanciaKm, acierto))
        if (acierto) resultadoSitio[sitioId] = true
        seleccionJugador[sitioId] = latitud to longitud
    }

    fun generarHistorial(lugares: List<Lugar>, niveles: Map<Int, Nivel>): List<Partida> {
        return historialSitios.mapNotNull { (sitioId, intentos) ->
            val lugar = lugares.find { it.id == sitioId } ?: return@mapNotNull null
            val nivel = niveles[sitioId] ?: return@mapNotNull null
            val completado = resultadoSitio.containsKey(sitioId)
            val conseguido = intentos.any { it.acierto }
            val puntuacion = intentos.count { it.acierto } * 100
            Partida(
                lugar = lugar,
                nivel = nivel,
                intentosRes = intentos.size,
                ayudasRes = 0,
                completado = completado,
                conseguido = conseguido,
                puntuacion = puntuacion,
                intentos = intentos.toList()
            )
        }
    }

    fun generarHistorialParaSitio(sitio: Lugar, nivel: Nivel): List<Partida> {
        val intentos = historialSitios[sitio.id] ?: emptyList()
        val completado = resultadoSitio.containsKey(sitio.id)
        val conseguido = intentos.any { it.acierto }
        val puntuacion = intentos.count { it.acierto } * 100
        return listOf(
            Partida(
                lugar = sitio,
                nivel = nivel,
                intentosRes = intentos.size,
                ayudasRes = 0,
                completado = completado,
                conseguido = conseguido,
                puntuacion = puntuacion,
                intentos = intentos.toList()
            )
        )
    }
}