package com.mycompany.juegogeolocalizacion.datos

import androidx.room.PrimaryKey

data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jugador: String,
    val puntuacion: Int,
    val fecha: Long
)