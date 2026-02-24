package com.mycompany.juegogeolocalizacion.datos

data class ResultadoIntento(
    val numero: Int,
    val acertado: Boolean,
    val distanciaKm: Double,
    val direccion: Direccion
)

enum class Direccion { NORTE, SUR, ESTE, OESTE}