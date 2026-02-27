package com.mycompany.juegogeolocalizacion.datos

data class Puntuacion(
    val fecha: String,
    val puntuacionT: Int,
    val tiempoT: Int,
    val aciertos: Int,
    val historialAciertos: List<Partida> = emptyList()
)

