package com.mycompany.juegogeolocalizacion.pantallas

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido
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
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapOSM(
    sitio: Lugar,
    onSelectionConfirmed: ((selectedLat: Double, selectedLon: Double, radiusKm: Float) -> Unit)? = null,
    targetPoint: Pair<Double, Double>? = null,
    distanciaKm: Double? = null
) {
    val context = LocalContext.current
    var hasValidatedInternet by remember { mutableStateOf(isInternetValidated(context)) }

    if (!hasValidatedInternet) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.sin_conexion),
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

    var selectedPoint by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var radiusKm by remember { mutableStateOf(10f) }
    var currentSelectionOverlays by remember { mutableStateOf<List<Overlay>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(text = sitio.nombre, style = MaterialTheme.typography.titleLarge)
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        AndroidView(
            factory = { ctx ->
                val config = Configuration.getInstance()
                config.load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                config.userAgentValue = ctx.packageName

                val mapView = MapView(ctx).apply {
                    val osmHttps = XYTileSource(
                        "OSM-HTTPS", 0, 19, 256, ".png",
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

                val receiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        p?.let {
                            selectedPoint = Pair(it.latitude, it.longitude)
                            // Sonido al tocar mapa
                            CambiadorSonido.reproducirSonido(context, R.raw.tocar_mapa)
                        }
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?) = false
                }

                mapView.overlays.add(MapEventsOverlay(receiver))
                mapView
            },
            modifier = Modifier.weight(1f).fillMaxWidth(),
            update = { mapView ->
                if (currentSelectionOverlays.isNotEmpty()) {
                    mapView.overlays.removeAll(currentSelectionOverlays)
                    currentSelectionOverlays = emptyList()
                }
                currentSelectionOverlays = updateMapOverlays(mapView, selectedPoint, targetPoint, radiusKm)
            }
        )

        if (selectedPoint != null) {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.selecciona_radio), style = MaterialTheme.typography.titleMedium)
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                CambiadorSonido.reproducirSonido(context, R.raw.boton)
                                selectedPoint = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.cancelar))
                        }
                        FilledTonalButton(
                            onClick = {
                                selectedPoint?.let { point ->
                                    val distancia = targetPoint?.let { (tLat, tLon) ->
                                        val latDiff = point.first - tLat
                                        val lonDiff = point.second - tLon
                                        // Aproximación sencilla en grados
                                        sqrt(latDiff*latDiff + lonDiff*lonDiff)
                                    } ?: 999.0

                                    val esAcierto = distancia <= (radiusKm / 111.0) // 1 grado ~ 111 km
                                    if (esAcierto) {
                                        CambiadorSonido.reproducirSonido(context, R.raw.acierto)
                                    } else {
                                        CambiadorSonido.reproducirSonido(context, R.raw.fallo)
                                    }

                                    onSelectionConfirmed?.invoke(point.first, point.second, radiusKm)
                                }
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

private fun updateMapOverlays(
    mapView: MapView,
    selectedPoint: Pair<Double, Double>?,
    targetPoint: Pair<Double, Double>?,
    radiusKm: Float
): List<Overlay> {
    val overlays = mutableListOf<Overlay>()
    selectedPoint?.let { (lat, lon) ->
        val marker = Marker(mapView).apply {
            title = "SELECTION_MARKER"
            position = GeoPoint(lat, lon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        overlays.add(marker)

        val polygon = Polygon().apply {
            val points = mutableListOf<GeoPoint>()
            val steps = 60
            val radiusMeters = (radiusKm * 1000).toDouble()
            for (i in 0 until steps) {
                val bearing = i * (360.0 / steps)
                points.add(destinationPoint(lat, lon, radiusMeters, bearing))
            }
            setPoints(points)
            fillColor = 0x44FF0000
            strokeColor = 0xFFFF0000.toInt()
            strokeWidth = 2f
        }
        overlays.add(polygon)

        targetPoint?.let { (tLat, tLon) ->
            overlays.add(Marker(mapView).apply {
                title = "TARGET_MARKER"
                position = GeoPoint(tLat, tLon)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })
            overlays.add(Polyline(mapView).apply {
                setPoints(listOf(GeoPoint(lat, lon), GeoPoint(tLat, tLon)))
                color = 0xFF0000FF.toInt()
                width = 3f
            })
        }
    }
    mapView.overlays.addAll(overlays)
    mapView.invalidate()
    return overlays
}

private fun destinationPoint(lat: Double, lon: Double, distanceMeters: Double, bearingDegrees: Double): GeoPoint {
    val R = 6371000.0
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
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}