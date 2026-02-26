package com.mycompany.juegogeolocalizacion.datos

import android.util.Log
import androidx.compose.runtime.mutableStateListOf

object PuntuacionesRepo {
    private val lista = mutableStateListOf<Puntuacion>()

    init {
        Log.d("PuntuacionesRepo", "Repositorio inicializado")
    }

    fun guardarPuntuacion(p: Puntuacion){
        Log.d("PuntuacionesRepo", "Guardando puntuación: Fecha=${p.fecha}, Puntos=${p.puntuacionT}, Tiempo=${p.tiempoT}s, Aciertos=${p.aciertos}")
        lista.add(p)
        Log.d("PuntuacionesRepo", "Puntuación guardada. Total de puntuaciones: ${lista.size}")
    }

    fun obtenerPuntuaciones(): List<Puntuacion> {
        Log.d("PuntuacionesRepo", "Obteniendo puntuaciones ordenadas. Total: ${lista.size}")
        val ordenadas = lista.sortedByDescending { it.puntuacionT }
        ordenadas.forEachIndexed { index, p ->
            Log.d("PuntuacionesRepo", "  [$index] Puntos=${p.puntuacionT}, Fecha=${p.fecha}")
        }
        return ordenadas
    }
}