package com.mycompany.juegogeolocalizacion.datos

import android.util.Log

object NivelActual {
    var nivel: Nivel? = null
        set(value) {
            if (value != null) {
                Log.d("NivelActual", "Nivel cambiado: ID=${value.id}, Nombre=${value.nombre}, Intentos=${value.intentos}, Radio=${value.radioKm}km")
            } else {
                Log.d("NivelActual", "Nivel establecido a null")
            }
            field = value
        }

    init {
        Log.d("NivelActual", "Objeto NivelActual inicializado")
    }
}