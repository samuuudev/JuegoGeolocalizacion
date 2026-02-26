package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido
import com.mycompany.juegogeolocalizacion.datos.NivelActual
import com.mycompany.juegogeolocalizacion.datos.Puntuacion
import com.mycompany.juegogeolocalizacion.datos.PuntuacionesRepo.guardarPuntuacion
import com.mycompany.juegogeolocalizacion.navegacion.Navegador
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun PantallaJuego(
    idSitio: Int,
    navController: NavController
) {
    Log.d("PantallaJuego", "Iniciando juego con idSitio=$idSitio")

    val sitio = ImagenesSitios.find { it.id == idSitio }
    val nivel = NivelActual.nivel

    // Validar sitio y nivel
    if (sitio == null) {
        Log.e("PantallaJuego", "ERROR: No se encontró sitio con ID=$idSitio")
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Error: No se encontró el sitio con ID $idSitio", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver")
            }
        }
        return
    }

    if (nivel == null) {
        Log.e("PantallaJuego", "ERROR: No hay nivel seleccionado")
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Error: No se ha seleccionado ningún nivel", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Volver")
            }
        }
        return
    }

    Log.d("PantallaJuego", "Sitio cargado: ${sitio.nombre} (lat=${sitio.latitud}, lon=${sitio.longitud})")
    Log.d("PantallaJuego", "Nivel: ${nivel.nombre}, Intentos=${nivel.intentos}")

    val fecha = System.currentTimeMillis().toString()
    var puntuacion by remember { mutableStateOf(0) }
    var intentos by remember { mutableStateOf(nivel.intentos) }
    var aciertos by remember { mutableStateOf(0) }
    var tiempo by remember { mutableStateOf(0) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        Log.d("PantallaJuego", "Composable montado - Iniciando temporizador")
        onDispose {
            Log.d("PantallaJuego", "Composable desmontado - Tiempo final: ${tiempo}s, Puntuación: $puntuacion")
        }
    }

    LaunchedEffect(Unit) {
        Log.d("PantallaJuego", "Temporizador iniciado")
        while (true) {
            delay(1000)
            tiempo++
            if (tiempo % 10 == 0) {
                Log.d("PantallaJuego", "Tiempo transcurrido: ${tiempo}s")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = sitio.imagen),
            contentDescription = sitio.nombre,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${stringResource(R.string.intentos_res)}: $intentos",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                CambiadorSonido.repoducirSonido(context, R.raw.boton)
                // TODO: Implementar ayuda IA
            }
        ) {
            Text(stringResource(R.string.ayuda))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            MapOSM(
                sitio = sitio,
                onMapTap = { latTap, lonTap ->
                    Log.d("PantallaJuego", "Tap en mapa: lat=$latTap, lon=$lonTap")
                    CambiadorSonido.repoducirSonido(context, R.raw.tocar_mapa)

                    if (intentos <= 0) {
                        Log.w("PantallaJuego", "No quedan intentos - Tap ignorado")
                        return@MapOSM
                    }

                    val distancia = calcularDistanciaKm(
                        latTap, lonTap,
                        sitio.latitud, sitio.longitud
                    )
                    Log.d("PantallaJuego", "Distancia calculada: ${"%.2f".format(distancia)} km")

                    val radioKm = when(nivel.id) {
                        1 -> 50.0
                        2 -> 25.0
                        3 -> 10.0
                        else -> 25.0
                    }
                    Log.d("PantallaJuego", "Radio del nivel: $radioKm km")

                    if (distancia <= radioKm) {
                        Log.i("PantallaJuego", "¡ACIERTO! Distancia dentro del radio")
                        aciertos++
                        val base = 1000
                        val intentosUsados = nivel.intentos - intentos + 1
                        val puntosGanados = maxOf(0, base - (intentosUsados - 1) * 150 - tiempo)
                        puntuacion += puntosGanados
                        Log.d("PantallaJuego", "Puntos ganados: $puntosGanados (Total: $puntuacion)")

                        mensajeDialogo = "¡Acertaste! +$puntosGanados puntos\nDistancia: ${"%.1f".format(distancia)} km"
                        mostrarDialogo = true
                    } else {
                        Log.i("PantallaJuego", "FALLO - Distancia fuera del radio")
                        val direccion = obtenerDireccion(latTap, lonTap, sitio.latitud, sitio.longitud)
                        Log.d("PantallaJuego", "Dirección correcta: $direccion")
                        mensajeDialogo = "Fallaste. Está más al $direccion\nDistancia: ${"%.1f".format(distancia)} km"
                        mostrarDialogo = true
                    }

                    intentos--
                    Log.d("PantallaJuego", "Intentos restantes: $intentos")

                    if (intentos == 0) {
                        Log.i("PantallaJuego", "Juego terminado - Guardando puntuación")
                        Log.d("PantallaJuego", "Resultados finales: Puntos=$puntuacion, Tiempo=${tiempo}s, Aciertos=$aciertos")

                        guardarPuntuacion(
                            Puntuacion(
                                fecha = fecha,
                                puntuacionT = puntuacion,
                                tiempoT = tiempo,
                                aciertos = aciertos
                            )
                        )
                        Log.d("PantallaJuego", "Navegando a pantalla de puntuaciones")
                        navController.navigate(Navegador.PanelPunt.ruta)
                    }
                }
            )
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text(if (aciertos > 0) "¡Correcto!" else "Intenta de nuevo") },
            text = { Text(mensajeDialogo) },
            confirmButton = {
                Button(onClick = { mostrarDialogo = false }) {
                    Text("OK")
                }
            }
        )
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
