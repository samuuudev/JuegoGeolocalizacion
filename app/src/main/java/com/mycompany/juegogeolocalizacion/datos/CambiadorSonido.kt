package com.mycompany.juegogeolocalizacion.datos

import android.content.Context
import android.media.MediaPlayer

object CambiadorSonido {
    private var sonidoActivado: Boolean = true

    fun setSonidoActivado(activado: Boolean) {
        sonidoActivado = activado

    }

    fun repoducirSonido(context: Context, sonido: Int) {
        if(!sonidoActivado) return

        val media = MediaPlayer.create(context, sonido)
        media.setOnCompletionListener  { it.release() }
        media.start()
    }
}