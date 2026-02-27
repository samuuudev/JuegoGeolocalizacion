package com.mycompany.juegogeolocalizacion.datos

data class Record(
    val id: Int = 0,
    val jugador: String,
    val puntuacion: Int,
    val fecha: Long
)