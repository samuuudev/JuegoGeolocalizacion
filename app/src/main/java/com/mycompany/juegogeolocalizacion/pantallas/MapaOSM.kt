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
    Log.d("MapOSM", "Inicializando mapa para sitio: ${sitio.nombre}")

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
            Log.d("MapOSM", "Creando MapView")

            // Diagnóstico de red
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            val validated = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
            Log.d("MapOSM", "Estado de red: activeNetwork=${activeNetwork != null}, internet=$hasInternet, validated=$validated")

            // Configurar OSMDroid correctamente
            val config = Configuration.getInstance()
            config.load(
                context,
                context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
            )
            // Establecer User-Agent para evitar ser bloqueado por el servidor
            config.userAgentValue = context.packageName
            Log.d("MapOSM", "User-Agent configurado: ${config.userAgentValue}")

            val mapView = MapView(context).apply {
                // Usar un tile source HTTPS con varios hosts para fallback DNS
                val osmHttps = XYTileSource(
                    "OSM-HTTPS",
                    0, 19, 256, ".png",
                    arrayOf(
                        "https://a.tile.openstreetmap.org/",
                        "https://b.tile.openstreetmap.org/",
                        "https://c.tile.openstreetmap.org/",
                        "https://tile.openstreetmap.de/"
                    )
                )
                setTileSource(osmHttps)
                Log.d("MapOSM", "TileSource establecido: OSM-HTTPS (org/de)")

                // Habilitar controles multitáctiles
                setMultiTouchControls(true)

                // Habilitar zoom con botones
                setBuiltInZoomControls(false)

                // Configurar nivel de zoom y centro
                controller.setZoom(6.0)
                controller.setCenter(GeoPoint(40.4168, -3.7038))

                // Establecer límites mínimos y máximos de zoom
                minZoomLevel = 3.0
                maxZoomLevel = 20.0

                Log.d("MapOSM", "Mapa centrado en España (40.4168, -3.7038) con zoom 6")
                Log.d("MapOSM", "Zoom mín: $minZoomLevel, máx: $maxZoomLevel")
            }

            val overlay = object : Overlay() {
                override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
                    e?.let { event ->
                        mapView?.let { map ->
                            val projection = map.projection
                            val geoPoint = projection.fromPixels(
                                event.x.toInt(),
                                event.y.toInt()
                            ) as GeoPoint

                            Log.d("MapOSM", "Tap detectado en: lat=${geoPoint.latitude}, lon=${geoPoint.longitude}")
                            onMapTap(geoPoint.latitude, geoPoint.longitude)
                        }
                    }
                    return true
                }
            }

            mapView.overlays.add(overlay)
            Log.d("MapOSM", "Overlay de tap añadido al mapa")

            // Forzar invalidación para que dibuje
            mapView.invalidate()
            Log.d("MapOSM", "MapView invalidado - Iniciando carga de tiles")

            mapView
        },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            Log.d("MapOSM", "Actualizando mapa para sitio: ${sitio.nombre}")
            mapView.invalidate()
        }
    )
}

private fun isInternetValidated(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
