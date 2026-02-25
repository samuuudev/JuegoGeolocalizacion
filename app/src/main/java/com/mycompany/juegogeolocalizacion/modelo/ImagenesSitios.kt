package com.mycompany.juegogeolocalizacion.modelo

import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.Lugar

val ImagenesSitios = listOf(
    Lugar(
        id = 1,
        nombre = "Torre Eiffel",
        ciudad = "París",
        descripcion = "Estructura metálica emblemática construida a finales del siglo XIX, reconocida por su diseño innovador y su gran altura",
        imagen = R.drawable.torre,
        video = 0,
        latitud = 48.8584,
        longitud = 2.2945
    ),

    Lugar(
        id = 2,
        nombre = "Coliseo Romano",
        ciudad = "Roma",
        descripcion = "Antiguo anfiteatro de gran tamaño utilizado para espectáculos públicos y eventos multitudinarios en la antigüedad",
        imagen = R.drawable.coliseo,
        video = 0,
        latitud = 41.8902,
        longitud = 12.4922
    ),

    Lugar(
        id = 3,
        nombre = "Estatua de la Libertad",
        ciudad = "Nueva York",
        descripcion = "Escultura monumental que representa una figura femenina sosteniendo una antorcha como símbolo de esperanza y libertad",
        imagen = R.drawable.estatua,
        video = 0,
        latitud = 40.6892,
        longitud = -74.0445
    ),

    Lugar(
        id = 1,
        nombre = "Plaza Mayor",
        ciudad = "Madrid",
        descripcion = "Amplio espacio público rodeado de edificios históricos, utilizado tradicionalmente para reuniones y celebraciones",
        imagen = R.drawable.plaza,
        video = 0,
        latitud =  40.4155,
        longitud = -3.7074
    ),

    Lugar(
        id = 1,
        nombre = "Cristo Redentor",
        ciudad = "Río de Janeiro",
        descripcion = "Imponente estatua de gran altura situada en lo alto de una montaña, con los brazos extendidos en gesto de bienvenida",
        imagen = R.drawable.torre,
        video = 0,
        latitud = -22.9519,
        longitud = -43.2105
    )
)