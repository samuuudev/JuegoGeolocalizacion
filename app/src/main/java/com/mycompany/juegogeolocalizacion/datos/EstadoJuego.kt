package com.mycompany.juegogeolocalizacion.datos

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

object EstadoJuego {
    var nombreJugador = mutableStateOf("")

    val resultadoSitio = mutableStateMapOf<Int, Boolean>()

    val seleccionJugador = mutableMapOf<Int, Pair<Double, Double>>()

    fun juegoCompletado(totalSitios: Int): Boolean {
        return resultadoSitio.size == totalSitios
    }
}

