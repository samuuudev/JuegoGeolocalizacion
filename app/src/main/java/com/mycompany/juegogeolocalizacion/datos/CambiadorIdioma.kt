package com.mycompany.juegogeolocalizacion.datos

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import java.util.Locale

object CambiadorIdioma {
    init {
        Log.d("CambiadorIdioma", "Sistema de cambio de idioma inicializado")
    }

    fun cambiarIdioma(context: Context, codigo: String) {
        Log.d("CambiadorIdioma", "Cambiando idioma a: $codigo")

        try {
            val locale = Locale(codigo)
            Locale.setDefault(locale)
            Log.d("CambiadorIdioma", "Locale por defecto establecido: ${locale.displayLanguage}")

            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)

            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            Log.d("CambiadorIdioma", "Configuración de recursos actualizada correctamente")
        } catch (e: Exception) {
            Log.e("CambiadorIdioma", "ERROR al cambiar idioma: ${e.message}", e)
        }
    }
}