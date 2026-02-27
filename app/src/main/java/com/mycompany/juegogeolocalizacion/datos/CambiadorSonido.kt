package com.mycompany.juegogeolocalizacion.datos

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

object CambiadorSonido {
    private var sonidoActivado: Boolean = true

    init {
        Log.d("CambiadorSonido", "Sistema de sonido inicializado - Estado: activado=$sonidoActivado")
    }

    fun setSonidoActivado(activado: Boolean) {
        Log.d("CambiadorSonido", "Cambio de estado de sonido: $sonidoActivado -> $activado")
        sonidoActivado = activado
    }

    fun reproducirSonido(context: Context, sonido: Int) {  // ← typo corregido
        if (!sonidoActivado) {
            Log.d("CambiadorSonido", "Sonido desactivado - No se reproduce sonido ID=$sonido")
            return
        }

        Log.d("CambiadorSonido", "Reproduciendo sonido ID=$sonido")
        try {
            val media = MediaPlayer.create(context, sonido)
            media?.apply {
                setOnCompletionListener { it.release() }
                start()
            } ?: Log.e("CambiadorSonido", "ERROR: No se pudo crear MediaPlayer para sonido ID=$sonido")
        } catch (e: Exception) {
            Log.e("CambiadorSonido", "ERROR reproduciendo sonido ID=$sonido: ${e.message}", e)
        }
    }
}