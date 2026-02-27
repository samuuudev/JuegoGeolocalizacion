package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.*
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios
import kotlinx.coroutines.delay
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaJuego(
    idSitio: Int,
    navController: NavController
) {

    val context = LocalContext.current
    val sitio = ImagenesSitios.find { it.id == idSitio }
    val nivel = NivelActual.nivel

    if (sitio == null || nivel == null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Error cargando partida", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(onClick = {
                CambiadorSonido.reproducirSonido(context, R.raw.boton)
                navController.popBackStack()
            }) { Text("Volver") }
        }
        return
    }

    val yaJugado = EstadoJuego.resultadoSitio.containsKey(sitio.id)
    val seleccionGuardada = EstadoJuego.seleccionJugador[sitio.id]

    var puntuacion by remember { mutableStateOf(0) }
    var intentos by remember { mutableStateOf(nivel.intentos) }
    var aciertos by remember { mutableStateOf(0) }
    var tiempo by remember { mutableStateOf(0) }

    var mostrarDialogoAI by remember { mutableStateOf(false) }
    var ayudasUsadas by remember { mutableStateOf(0) }

    var ultimaSeleccion by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var ultimaDistancia by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(Unit) {
        if (!yaJugado) {
            while (true) {
                delay(1000)
                tiempo++
            }
        }
    }

    LaunchedEffect(yaJugado) {
        if (yaJugado && seleccionGuardada != null) {
            ultimaSeleccion = seleccionGuardada
            ultimaDistancia = calcularDistanciaKm(
                seleccionGuardada.first,
                seleccionGuardada.second,
                sitio.latitud,
                sitio.longitud
            )
            intentos = 0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        MapOSM(
            sitio = sitio,
            targetPoint = if (intentos == 0 && ultimaSeleccion != null)
                Pair(sitio.latitud, sitio.longitud)
            else null,
            distanciaKm = if (intentos == 0) ultimaDistancia else null,
            onSelectionConfirmed = if (yaJugado) null else { latSel, lonSel, _ ->

                if (intentos <= 0) return@MapOSM

                ultimaSeleccion = Pair(latSel, lonSel)
                CambiadorSonido.reproducirSonido(context, R.raw.acierto)

                val distancia = calcularDistanciaKm(
                    latSel,
                    lonSel,
                    sitio.latitud,
                    sitio.longitud
                )

                val radioNivel = when (nivel.id) {
                    1 -> 50.0
                    2 -> 25.0
                    3 -> 10.0
                    else -> 25.0
                }

                val esAcierto = distancia <= radioNivel
                ultimaDistancia = distancia

                if (esAcierto) {
                    aciertos++
                    val puntosGanados = maxOf(0, 1000 - (nivel.intentos - intentos) * 150 - tiempo)
                    puntuacion += puntosGanados
                    intentos = 0

                    EstadoJuego.registrarIntento(sitio.id, latSel, lonSel, distancia, esAcierto)

                    navController.navigate("video/${sitio.id}")
                } else {
                    intentos--
                    EstadoJuego.registrarIntento(sitio.id, latSel, lonSel, distancia, esAcierto)
                }

                if (intentos == 0 && !esAcierto) {
                    val nuevaPuntuacion = Puntuacion(
                        fecha = System.currentTimeMillis().toString(),
                        puntuacionT = puntuacion,
                        tiempoT = tiempo,
                        aciertos = aciertos,
                        historialAciertos = EstadoJuego.generarHistorialParaSitio(sitio, nivel)
                    )
                    PuntuacionesRepo.guardarPuntuacion(context, EstadoJuego.nombreJugador.value, nuevaPuntuacion)
                }
            }
        )

        if (ultimaDistancia != null) {
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                Surface(
                    tonalElevation = 8.dp,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(
                        text = if (yaJugado)
                            "📌 Distancia final: ${"%.2f".format(ultimaDistancia)} km"
                        else if (ultimaDistancia == 0.0)
                            "🎯 ¡Dentro del radio! "
                        else
                            "📍 Te faltaron ${"%.2f".format(ultimaDistancia)} km",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        FilledTonalButton(
            onClick = {
                CambiadorSonido.reproducirSonido(context, R.raw.boton)
                mostrarDialogoAI = true
            },
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            enabled = intentos > 0 && ayudasUsadas < nivel.ayuda
        ) { Text("🤖 ${nivel.ayuda - ayudasUsadas}/${nivel.ayuda}") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {

            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.height(120.dp).fillMaxWidth(0.4f)
            ) {
                Image(
                    painter = painterResource(id = sitio.imagen),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                tonalElevation = 8.dp,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Intentos")
                        Text(
                            if (yaJugado) "Finalizado" else "$intentos",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.tiempo))
                        Text(
                            if (yaJugado) "-" else "${tiempo}s",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}

fun calcularDistanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}