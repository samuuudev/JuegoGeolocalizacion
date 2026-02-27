package com.mycompany.juegogeolocalizacion.datos

data class ResultadoIntento(
    val latitud: Double,
    val longitud: Double,
    val distanciaKm: Double,
    val acierto: Boolean
)