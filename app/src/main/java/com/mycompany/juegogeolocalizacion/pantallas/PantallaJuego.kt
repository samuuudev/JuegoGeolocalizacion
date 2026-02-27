package com.mycompany.juegogeolocalizacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios
import com.mycompany.juegogeolocalizacion.datos.NivelActual
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

    var mostrarDialogoFinal by remember { mutableStateOf(false) }
    var mensajeDialogoFinal by remember { mutableStateOf("") }

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

    Scaffold { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            MapOSM(
                sitio = sitio,
                targetPoint = if (ultimaSeleccion != null)
                    Pair(sitio.latitud, sitio.longitud)
                else null,
                distanciaKm = ultimaDistancia,
                onSelectionConfirmed = { latSel, lonSel, _ ->

                    if (intentos <= 0) return@MapOSM

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
                        ultimaSeleccion = Pair(latSel, lonSel)
                        ultimaDistancia = distancia

                        mensajeDialogoFinal =
                            "Distancia: %.2f km\nAciertos: $aciertos\nPuntuación: $puntuacion"
                                .format(distancia)

                        mostrarDialogoFinal = true
                    }
                }
            )

            FilledTonalButton(
                onClick = { mostrarDialogoAI = true },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                enabled = intentos > 0 && ayudasUsadas < nivel.ayuda
            ) {
                Text("🤖 ${nivel.ayuda - ayudasUsadas}/${nivel.ayuda}")
            }

            Card(
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
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

            Surface(
                tonalElevation = 8.dp,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(48.dp)
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Intentos", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "$intentos",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tiempo", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "${tiempo}s",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            if (mostrarDialogoFinal) {
                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {
                        FilledTonalButton(
                            onClick = {
                                navController.navigate(Navegador.PanelPunt.ruta)
                            }
                        ) {
                            Text("Siguiente")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { mostrarDialogoFinal = false }
                        ) {
                            Text("Explorar mapa")
                        }
                    },
                    title = {
                        Text("Resultado Final")
                    },
                    text = {
                        Text(mensajeDialogoFinal)
                    }
                )
            }

            if (mostrarDialogoAI) {
                DialogoPistasIA(
                    pistas = pistasAI,
                    cargando = cargandoAI,
                    error = errorAI,
                    onDismiss = { mostrarDialogoAI = false }
                )
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

fun obtenerDireccion(latTap: Double, lonTap: Double, latReal: Double, lonReal: Double): String {
    val ns = when {
        latTap < latReal -> "norte"
        latTap > latReal -> "sur"
        else -> ""
    }
    val eo = when {
        lonTap < lonReal -> "este"
        lonTap > lonReal -> "oeste"
        else -> ""
    }
    return listOf(ns, eo).filter { it.isNotEmpty() }.joinToString(" y ")
}

fun obtenerDireccionEnum(latTap: Double, lonTap: Double, latReal: Double, lonReal: Double): com.mycompany.juegogeolocalizacion.datos.Direccion {
    val diffLat = latReal - latTap
    val diffLon = lonReal - lonTap
    return if (abs(diffLat) >= abs(diffLon)) {
        if (diffLat > 0) com.mycompany.juegogeolocalizacion.datos.Direccion.NORTE else com.mycompany.juegogeolocalizacion.datos.Direccion.SUR
    } else {
        if (diffLon > 0) com.mycompany.juegogeolocalizacion.datos.Direccion.ESTE else com.mycompany.juegogeolocalizacion.datos.Direccion.OESTE
    }
}
