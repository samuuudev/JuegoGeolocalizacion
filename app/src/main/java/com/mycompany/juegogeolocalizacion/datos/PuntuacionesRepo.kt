package com.mycompany.juegogeolocalizacion.datos

import androidx.compose.runtime.mutableStateListOf

object PuntuacionesRepo {
    private val lista = mutableStateListOf<Puntuacion>()

    fun guardarPuntuacion(p: Puntuacion){
        lista.add(p)
    }

    fun obtenerPuntuaciones(): List<Puntuacion> {
        return lista.sortedByDescending { it.puntuacionT }
    }
}