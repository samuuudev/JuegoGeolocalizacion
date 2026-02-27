package com.mycompany.juegogeolocalizacion.datos

import android.util.Log
import androidx.compose.runtime.mutableStateListOf

object PuntuacionesRepo {
    private val lista = mutableStateListOf<Pair<String, Puntuacion>>()

    init {
        Log.d("PuntuacionesRepo", "Repositorio inicializado")
    }

    fun guardarPuntuacion(nombre: String, p: Puntuacion){
        Log.d("PuntuacionesRepo", "Guardando puntuación: Fecha=${p.fecha}, Puntos=${p.puntuacionT}, Tiempo=${p.tiempoT}s, Aciertos=${p.aciertos}")
        lista.add(nombre to p)
        Log.d("PuntuacionesRepo", "Puntuación guardada. Total de puntuaciones: ${lista.size}")
    }

    fun obtenerPuntuaciones(): List<Pair<String, Puntuacion>> {
        Log.d("PuntuacionesRepo", "Obteniendo puntuaciones ordenadas. Total: ${lista.size}")
        val ordenadas = lista.sortedByDescending { it.second.puntuacionT }
        ordenadas.forEachIndexed { index,(nombre, p) ->
            Log.d("PuntuacionesRepo","[$index] Jugador=$nombre, Puntos=${p.puntuacionT}"
            )
        }
        return ordenadas
    }
}