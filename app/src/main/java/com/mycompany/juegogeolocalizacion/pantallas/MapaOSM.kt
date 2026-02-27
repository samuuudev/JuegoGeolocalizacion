package com.mycompany.juegogeolocalizacion.pantallas

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.Slider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.Lugar
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapOSM(
    sitio: Lugar,
    // callback para avisar a la pantalla padre con la selección: lat, lon y radio (km)
    onSelectionConfirmed: ((selectedLat: Double, selectedLon: Double, radiusKm: Float) -> Unit)? = null,
    // Aceptan nulos ya que inicialmente no hay seleccion, ya se cambiaran. Y la distancia se calculara al seleccionar un punto
    targetPoint: Pair<Double, Double>? = null,
    distanciaKm: Double? = null
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
                text = stringResource(R.string.conexion),
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

    // Seleccion del punto y radio de margen en km, de 0 a 10
    var selectedPoint by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var radiusKm by remember { mutableStateOf(10f) } // 1..10 km

    // Guardamos referencias a las overlays que añadimos para poder eliminarlas al actualizar la selección
    var currentSelectionOverlays by remember { mutableStateOf<List<Overlay>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = sitio.nombre,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        AndroidView(
            factory = { ctx ->
                Log.d("MapOSM", "Creando MapView")

                // Diagnóstico de red
                val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                val validated = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
                Log.d("MapOSM", "Estado de red: activeNetwork=${activeNetwork != null}, internet=${hasInternet}, validated=${validated}")

                // Configurar OSMDroid correctamente
                val config = Configuration.getInstance()
                config.load(
                    ctx,
                    ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
                )
                config.userAgentValue = ctx.packageName
                Log.d("MapOSM", "User-Agent configurado: ${config.userAgentValue}")

                val mapView = MapView(ctx).apply {
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
                    setMultiTouchControls(true)
                    setBuiltInZoomControls(true)
                    isHorizontalMapRepetitionEnabled = false
                    setTilesScaledToDpi(true)
                    controller.setZoom(6.5)
                    controller.setCenter(GeoPoint(40.4168, -3.7038))
                    minZoomLevel = 3.0
                    maxZoomLevel = 20.0
                }

                // Usamos MapEventsReciver para detectar taps en el mapa y actualizar la seleccion
                val receiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        if (p != null) {
                            Log.d("MapOSM", "map tap: lat=${p.latitude}, lon=${p.longitude}")
                            // actualiza el punto seleccionado, y redibuja el mapa con el nuevo punto y el radio
                            selectedPoint = Pair(p.latitude, p.longitude)
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?): Boolean {
                        return false
                    }
                }

                mapView.overlays.add(MapEventsOverlay(receiver))
                Log.d("MapOSM", "MapEventsOverlay añadido")

                mapView
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            update = { mapView ->
                Log.d("MapOSM", "update MapView invoked. selectedPoint=$selectedPoint, radiusKm=$radiusKm, targetPoint=$targetPoint")

                // Limpiar overlays previos de selección (marcadores y polígono)
                if (currentSelectionOverlays.isNotEmpty()) {
                    mapView.overlays.removeAll(currentSelectionOverlays)
                    currentSelectionOverlays = emptyList()
                }

                // Si hay selectedPoint Y targetPoint, dibujar línea entre ellos (primero, para que aparezca debajo)
                if (selectedPoint != null && targetPoint != null) {
                    val sLat = selectedPoint!!.first
                    val sLon = selectedPoint!!.second
                    val tLat = targetPoint.first
                    val tLon = targetPoint.second

                    // Marcador selección
                    val selMarker = Marker(mapView).apply {
                        title = "SELECTION_MARKER"
                        position = GeoPoint(sLat, sLon)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }

                    // Marcador objetivo (TARGET)
                    val targetMarker = Marker(mapView).apply {
                        title = "TARGET_MARKER"
                        position = GeoPoint(tLat, tLon)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }

                    // Línea (Polyline) entre selección y objetivo
                    val line = Polyline(mapView).apply {
                        setPoints(
                            listOf(
                                GeoPoint(sLat, sLon),
                                GeoPoint(tLat, tLon)
                            )
                        )
                        color = 0xFF0000FF.toInt() // azul
                        width = 3f
                    }

                    mapView.overlays.add(line)
                    mapView.overlays.add(selMarker)
                    mapView.overlays.add(targetMarker)
                    currentSelectionOverlays = listOf(line, selMarker, targetMarker)

                    Log.d("MapOSM", "Línea dibujada entre ($sLat, $sLon) y ($tLat, $tLon). Distancia: $distanciaKm km")
                } else if (selectedPoint != null) {
                    // Si solo hay selectedPoint (sin confirmación aún), dibujar marcador + círculo de selección pero sin linea
                    val lat = selectedPoint!!.first
                    val lon = selectedPoint!!.second

                    val marker = Marker(mapView).apply {
                        title = "SELECTION_MARKER"
                        position = GeoPoint(lat, lon)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }

                    // dibuja un circulo usando Polygon
                    val polygon = Polygon().apply {
                        // generar puntos alrededor del centro
                        val points = mutableListOf<GeoPoint>()
                        val radiusMeters = (radiusKm * 1000).toDouble()
                        val steps = 60
                        for (i in 0 until steps) {
                            val bearing = i * (360.0 / steps)
                            val p = destinationPoint(lat, lon, radiusMeters, bearing)
                            points.add(p)
                        }
                        setPoints(points)
                        fillColor = 0x44FF0000.toInt()
                        strokeColor = 0xFFFF0000.toInt()
                        strokeWidth = 2f
                    }

                    mapView.overlays.add(marker)
                    mapView.overlays.add(polygon)
                    currentSelectionOverlays = listOf(marker, polygon)
                }

                mapView.invalidate()
            }
        )

        if (selectedPoint != null) {

            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Selecciona el radio",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${String.format(Locale.getDefault(), "%.1f", radiusKm)} km",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = radiusKm,
                        onValueChange = { radiusKm = it },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedButton(
                            onClick = { selectedPoint = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancelar))
                        }

                        FilledTonalButton(
                            onClick = {
                                val (sLat, sLon) = selectedPoint!!
                                onSelectionConfirmed?.invoke(sLat, sLon, radiusKm)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.confirmar))
                        }
                    }
                }
            }
        }
    }
}

// Calcular punto destino usando fórmula esférica dado lat/lon en grados, distancia (m) y bearing (grados)
private fun destinationPoint(lat: Double, lon: Double, distanceMeters: Double, bearingDegrees: Double): GeoPoint {
    val R = 6371000.0 // radio de la Tierra en metros
    val bearing = Math.toRadians(bearingDegrees)
    val lat1 = Math.toRadians(lat)
    val lon1 = Math.toRadians(lon)
    val dR = distanceMeters / R

    val lat2 = Math.asin(Math.sin(lat1) * Math.cos(dR) + Math.cos(lat1) * Math.sin(dR) * Math.cos(bearing))
    val lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(dR) * Math.cos(lat1), Math.cos(dR) - Math.sin(lat1) * Math.sin(lat2))

    return GeoPoint(Math.toDegrees(lat2), Math.toDegrees(lon2))
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
