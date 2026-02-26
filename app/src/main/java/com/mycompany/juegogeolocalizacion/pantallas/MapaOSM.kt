package com.mycompany.juegogeolocalizacion.pantallas

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mycompany.juegogeolocalizacion.datos.Lugar
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

@Composable
fun MapOSM(
    sitio: Lugar,
    onMapTap: (lat: Double, lon: Double) -> Unit
) {
    Log.d("MapOSM", "Inicializando mapa para sitio: ${'$'}{sitio.nombre}")

    val context = LocalContext.current
    val hasValidatedInternet = remember { isInternetValidated(context) }
    if (!hasValidatedInternet) {
        Log.w("MapOSM", "Conectividad no validada. Es probable que el DNS o internet no estén disponibles.")
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sin conexión a internet o DNS no disponible.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Conéctate a una red con acceso a internet para cargar el mapa.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    DisposableEffect(Unit) {
        Log.d("MapOSM", "Mapa montado")
        onDispose {
            Log.d("MapOSM", "Mapa desmontado")
        }
    }

    AndroidView(
        factory = { context ->
            Log.d("MapOSM", "Creando MapView (backup)")
            // ...contenido original ... (guardado en backup)
            MapView(context)
        },
        modifier = Modifier.fillMaxSize(),
        update = { mapView -> Log.d("MapOSM", "Update backup mapView") }
    )
}

private fun isInternetValidated(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
