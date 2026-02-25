package com.mycompany.juegogeolocalizacion.pantallas

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mycompany.juegogeolocalizacion.datos.Lugar
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay

@Composable
fun MapOSM(
    sitio: Lugar,
    onMapTap: () -> Unit
) {
    AndroidView(
        modifier = Modifier,
        factory = { context: Context ->
            MapView(context).apply {
                // Configuramos el mapa
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                // Le metemos zoom y lo colocamos
                controller.setZoom(5.0)
                controller.setCenter(GeoPoint(40.0, -3.7))

                // Listener para las veces que tocamos la pantalla
                val toques = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                        onMapTap()
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint?) = false
                }

                overlays.add(MapEventsOverlay(toques))
            }
        }
    )
}