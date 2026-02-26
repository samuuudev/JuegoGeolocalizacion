package com.mycompany.juegogeolocalizacion.pantallas

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
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
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

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
            Log.d("MapOSM", "Estado de red: activeNetwork=${activeNetwork != null}, internet=${hasInternet}, validated=${validated}")

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

                // Habilitar zoom con botones (built-in)
                setBuiltInZoomControls(true)

                // Ajustes para mejorar el renderizado y la interacción
                isHorizontalMapRepetitionEnabled = false
                setTilesScaledToDpi(true)

                // Ajustar nivel de zoom inicial más cercano para que el mapa no aparezca "muy pequeño"
                controller.setZoom(6.5)
                controller.setCenter(GeoPoint(40.4168, -3.7038)) // Establecemos como centro España

                // Establecer límites mínimos y máximos de zoom
                minZoomLevel = 3.0
                maxZoomLevel = 20.0

                Log.d("MapOSM", "Mapa configurado: centro España (40.4168, -3.7038) con zoom inicial=6.5")
                Log.d("MapOSM", "Zoom mín: $minZoomLevel, máx: $maxZoomLevel")
            }

            // Usar MapEventsOverlay para capturar tap y long-press de forma robusta
            val mapEventsReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    if (p != null) {
                        Log.d("MapOSM", "singleTapConfirmedHelper lat=${p.latitude} lon=${p.longitude}")
                        onMapTap(p.latitude, p.longitude)
                    }
                    return true
                }

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    // No necesitamos manejo de long press por ahora
                    Log.d("MapOSM", "longPressHelper invoked at ${p?.latitude}, ${p?.longitude}")
                    return false
                }
            }

            mapView.overlays.add(MapEventsOverlay(mapEventsReceiver))
            Log.d("MapOSM", "MapEventsOverlay añadido al MapView")

            // Forzar invalidación para que dibuje
            mapView.invalidate()
            Log.d("MapOSM", "MapView invalidado - Iniciando carga de tiles")

            mapView
        },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            Log.d("MapOSM", "Actualizando mapa para sitio: ${sitio.nombre}")
            // Si en el futuro quieres reposicionar el mapa al seleccionar un sitio, hazlo aquí
            mapView.invalidate()
        }
    )
}

private fun isInternetValidated(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    val validated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    Log.d("MapOSM", "Comprobación de red: hasInternet=$hasInternet, validated=$validated")
    return hasInternet && validated
}
