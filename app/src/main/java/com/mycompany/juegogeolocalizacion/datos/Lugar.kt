package com.mycompany.juegogeolocalizacion.datos

data class Lugar(
    val id: Int,
    val nombre: String,
    val ciudad: String,
    val descripcion: String,
    val imagen: Int,
    val latitud: Double,
    val longitud: Double
)