package com.mycompany.juegogeolocalizacion.datos

data class Record(
    val nombreJugador: String,
    val fecha: String,
    val puntuacionTotal: Int,
    val tiempoTotalSegundos: Int,
    val aciertos: Int
)