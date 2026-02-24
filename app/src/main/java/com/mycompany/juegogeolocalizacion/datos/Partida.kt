package com.mycompany.juegogeolocalizacion.datos

data class Partida(
    val lugar: Lugar,
    val nivel: Nivel,
    val intentosRes: Int,
    val ayudasRes: Int,
    val completado: Boolean,
    val conseguido: Boolean,
    val puntuacion: Int,
    val intentos: List<ResultadoIntento>
)