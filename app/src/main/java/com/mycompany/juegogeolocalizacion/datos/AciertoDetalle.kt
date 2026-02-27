package com.mycompany.juegogeolocalizacion.datos

data class AciertoDetalle(
    val nombreSitio: String,
    val dificultad: String,
    val puntos: Int,
    val intentosUsados: Int,
    val distanciaKm: Double
)

