package com.mycompany.juegogeolocalizacion.datos

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object CambiadorIdioma {
    fun cambiarIdioma(context: Context, codigo: String) {
        val locale = Locale(codigo)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}