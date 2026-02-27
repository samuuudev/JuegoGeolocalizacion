package com.mycompany.juegogeolocalizacion.datos

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

object PuntuacionesRepo {

    private val lista = mutableStateListOf<Pair<String, Puntuacion>>()
    private const val PREFS_NAME = "puntuaciones_prefs"
    private const val KEY_PUNTUACIONES = "lista_puntuaciones"

    fun inicializar(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PUNTUACIONES, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Pair<String, Puntuacion>>>() {}.type
            val datos: List<Pair<String, Puntuacion>> = Gson().fromJson(json, type)
            lista.clear()
            lista.addAll(datos)
        }
        Log.d("PuntuacionesRepo", "Repositorio inicializado con ${lista.size} puntuaciones")
    }

    fun guardarPuntuacion(context: Context, nombre: String, p: Puntuacion) {
        lista.add(nombre to p)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(lista)
        prefs.edit().putString(KEY_PUNTUACIONES, json).apply()
        Log.d("PuntuacionesRepo", "Puntuación guardada: $nombre -> ${p.puntuacionT}")
    }

    fun obtenerPuntuaciones(): List<Pair<String, Puntuacion>> {
        return lista.sortedByDescending { it.second.puntuacionT }
    }
}