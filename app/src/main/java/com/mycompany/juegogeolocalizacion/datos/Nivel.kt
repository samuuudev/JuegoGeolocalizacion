package com.mycompany.juegogeolocalizacion.datos

data class Nivel(
    val id: Int,
    val nombre: String,
    val radioKm: Double,
    val intentos: Int,
    val ayuda: Int
)