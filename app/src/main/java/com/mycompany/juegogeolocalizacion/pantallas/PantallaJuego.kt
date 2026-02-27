package com.mycompany.juegogeolocalizacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycompany.juegogeolocalizacion.datos.EstadoJuego
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios
import com.mycompany.juegogeolocalizacion.datos.NivelActual
import com.mycompany.juegogeolocalizacion.datos.Puntuacion
import com.mycompany.juegogeolocalizacion.datos.PuntuacionesRepo
import com.mycompany.juegogeolocalizacion.navegacion.Navegador
import kotlinx.coroutines.delay
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaJuego(
    idSitio: Int,
    navController: NavController
) {

    val sitio = ImagenesSitios.find { it.id == idSitio }
    val nivel = NivelActual.nivel

    if (sitio == null || nivel == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Error cargando partida", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(onClick = { navController.popBackStack() }) {
                Text("Volver")
            }
        }
        return
    }

    var puntuacion by remember { mutableStateOf(0) }
    var intentos by remember { mutableStateOf(nivel.intentos) }
    var aciertos by remember { mutableStateOf(0) }
    var tiempo by remember { mutableStateOf(0) }

    var mostrarDialogoAI by remember { mutableStateOf(false) }
    var pistasAI by remember { mutableStateOf<com.mycompany.juegogeolocalizacion.datos.AIPistas?>(null) }
    var cargandoAI by remember { mutableStateOf(false) }
    var errorAI by remember { mutableStateOf<String?>(null) }
    var ayudasUsadas by remember { mutableStateOf(0) }

    var ultimaSeleccion by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var ultimaDistancia by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            tiempo++
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        MapOSM(
            sitio = sitio,
            targetPoint = if (ultimaSeleccion != null)
                Pair(sitio.latitud, sitio.longitud)
            else null,
            distanciaKm = ultimaDistancia,
            onSelectionConfirmed = { latSel, lonSel, _ ->

                if (intentos <= 0) return@MapOSM

                ultimaSeleccion = Pair(latSel, lonSel)

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
                val diferencia = maxOf(0.0, distancia - radioNivel)
                ultimaDistancia = diferencia

                if (esAcierto) {
                    aciertos++
                    val puntosGanados =
                        maxOf(0, 1000 - (nivel.intentos - intentos) * 150 - tiempo)
                    puntuacion += puntosGanados
                    intentos = 0
                } else {
                    intentos--
                }

                if (intentos == 0) {

                    EstadoJuego.resultadoSitio[sitio.id] = aciertos > 0

                    if (EstadoJuego.juegoCompletado(ImagenesSitios.size)) {

                        PuntuacionesRepo.guardarPuntuacion(
                            EstadoJuego.nombreJugador.value,
                            Puntuacion(
                                fecha = System.currentTimeMillis().toString(),
                                puntuacionT = puntuacion,
                                tiempoT = tiempo,
                                aciertos = aciertos
                            )
                        )
                    }

                    navController.navigate(Navegador.Carrousel.ruta) {
                        popUpTo(Navegador.Carrousel.ruta) { inclusive = true }
                    }
                }
            }
        )

        // ✅ MENSAJE DE DISTANCIA (BIEN PUESTO FUERA)
        if (ultimaDistancia != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Surface(
                    tonalElevation = 8.dp,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(
                        text = if (ultimaDistancia == 0.0)
                            "🎯 ¡Dentro del radio!"
                        else
                            "📍 Te faltaron ${"%.2f".format(ultimaDistancia)} km",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        // BOTÓN IA
        FilledTonalButton(
            onClick = { mostrarDialogoAI = true },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            enabled = intentos > 0 && ayudasUsadas < nivel.ayuda
        ) {
            Text("🤖 ${nivel.ayuda - ayudasUsadas}/${nivel.ayuda}")
        }

        // FOTO + INFO
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {

            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(0.4f)
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
                        Text("$intentos", style = MaterialTheme.typography.headlineSmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tiempo")
                        Text("${tiempo}s", style = MaterialTheme.typography.headlineSmall)
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