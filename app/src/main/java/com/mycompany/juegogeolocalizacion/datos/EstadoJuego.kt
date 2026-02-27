package com.mycompany.juegogeolocalizacion.datos

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

object EstadoJuego {
    // Nombre del jugador
    var nombreJugador = mutableStateOf("")

    // Resultado sitio
    val resultadoSitio = mutableStateMapOf<Int, Boolean>()

    fun juegoCompletado(totalSitios: Int): Boolean {
        return resultadoSitio.size == totalSitios
    }
}

