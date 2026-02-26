package com.mycompany.juegogeolocalizacion

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mycompany.juegogeolocalizacion.navegacion.AppNavegacion
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate: Iniciando aplicación")

        // Configurar OSMDroid
        initializeOSMDroid()

        enableEdgeToEdge()
        setContent {
            Log.d("MainActivity", "setContent: Cargando AppNavegacion")
            AppNavegacion()
        }
    }

    private fun initializeOSMDroid() {
        Log.d("MainActivity", "Inicializando OSMDroid")

        // Configuración global de OSMDroid
        val config = Configuration.getInstance()

        // Establecer User-Agent (requerido por los servidores de tiles)
        config.userAgentValue = packageName
        Log.d("MainActivity", "OSMDroid User-Agent: ${config.userAgentValue}")

        // Configurar caché en el almacenamiento interno
        config.osmdroidBasePath = getExternalFilesDir(null)
        config.osmdroidTileCache = getExternalFilesDir("osmdroid/tiles")
        Log.d("MainActivity", "OSMDroid caché configurada en: ${config.osmdroidTileCache}")

        // Cargar preferencias
        config.load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        Log.d("MainActivity", "OSMDroid inicializado correctamente")
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart: Activity iniciada")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume: Activity en primer plano")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause: Activity pausada")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop: Activity detenida")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy: Activity destruida")
    }
}