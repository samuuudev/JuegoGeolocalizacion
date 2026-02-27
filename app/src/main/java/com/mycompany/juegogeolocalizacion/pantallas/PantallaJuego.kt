package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido
import com.mycompany.juegogeolocalizacion.datos.NivelActual
import com.mycompany.juegogeolocalizacion.datos.Partida
import com.mycompany.juegogeolocalizacion.datos.Puntuacion
import com.mycompany.juegogeolocalizacion.datos.PuntuacionesRepo.guardarPuntuacion
import com.mycompany.juegogeolocalizacion.navegacion.Navegador
import kotlinx.coroutines.delay
import kotlin.math.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
            Text(
                text = "${stringResource(R.string.errorSitio)} $idSitio",
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.volver))
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
            Text(
                text = stringResource(R.string.errorNivel),
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.volver))
            }
        }
        return
    }

    Log.d(
        "PantallaJuego",
        "Sitio cargado: ${sitio.nombre} (lat=${sitio.latitud}, lon=${sitio.longitud})"
    )
    Log.d("PantallaJuego", "Nivel: ${nivel.nombre}, Intentos=${nivel.intentos}")

    val fecha = System.currentTimeMillis().toString()
    var puntuacion by remember { mutableStateOf(0) }
    var intentos by remember { mutableStateOf(nivel.intentos) }
    var aciertos by remember { mutableStateOf(0) }
    var tiempo by remember { mutableStateOf(0) }
    var mensajeEmergente by remember { mutableStateOf("") }
    var mostrarMensajeEmergente by remember { mutableStateOf(false) }
    var mostrarDialogoFinal by remember { mutableStateOf(false) }
    var mensajeDialogoFinal by remember { mutableStateOf("") }

    // Lista para registrar los intentos realizados
    var listaIntentos by remember { mutableStateOf<List<com.mycompany.juegogeolocalizacion.datos.ResultadoIntento>>(emptyList()) }
    val context = LocalContext.current

    // Estado para mostrar la línea entre selección y objetivo SOLO al final
    var ultimaSeleccion by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var ultimaDistancia by remember { mutableStateOf<Double?>(null) }

    DisposableEffect(Unit) {
        Log.d("PantallaJuego", "Composable montado - Iniciando temporizador")
        onDispose {
            Log.d(
                "PantallaJuego",
                "Composable desmontado - Tiempo final: ${tiempo}s, Puntuación: $puntuacion"
            )
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = sitio.nombre,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = sitio.imagen),
                    contentDescription = sitio.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            Surface(
                tonalElevation = 4.dp,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Intentos", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "$intentos",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Tiempo", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "${tiempo}s",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${stringResource(R.string.intentos_res)}: $intentos",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilledTonalButton(
                onClick = {
                    CambiadorSonido.repoducirSonido(context, R.raw.boton)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.ayuda))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                // Mapa
                MapOSM(
                    sitio = sitio,
                    targetPoint = if (ultimaSeleccion != null) Pair(sitio.latitud, sitio.longitud) else null,
                    distanciaKm = ultimaDistancia,
                    onSelectionConfirmed = { latSel, lonSel, radiusKm ->
                        Log.d("PantallaJuego", "Selección confirmada: lat=$latSel, lon=$lonSel, radioKm=$radiusKm")
                        CambiadorSonido.repoducirSonido(context, R.raw.tocar_mapa)

                        if (intentos <= 0) {
                            Log.w("PantallaJuego", "No quedan intentos - Selección ignorada")
                            return@MapOSM
                        }

                        val distancia = calcularDistanciaKm(latSel, lonSel, sitio.latitud, sitio.longitud)
                        Log.d("PantallaJuego", "Distancia calculada: ${"%.2f".format(distancia)} km")

                        val radioNivel = when (nivel.id) {
                            1 -> 50.0
                            2 -> 25.0
                            3 -> 10.0
                            else -> 25.0
                        }
                        Log.d("PantallaJuego", "Radio del nivel (${nivel.nombre}): $radioNivel km")

                        val esAcierto = distancia <= radioNivel
                        val direccionEnum = obtenerDireccionEnum(latSel, lonSel, sitio.latitud, sitio.longitud)

                        val intentoNum = listaIntentos.size + 1
                        val resultado = com.mycompany.juegogeolocalizacion.datos.ResultadoIntento(
                            numero = intentoNum,
                            acertado = esAcierto,
                            distanciaKm = distancia,
                            direccion = direccionEnum
                        )
                        listaIntentos = listaIntentos + resultado

                        if (esAcierto) {
                            Log.i("PantallaJuego", "¡ACIERTO! Distancia dentro del radio del nivel")
                            aciertos++
                            val base = 1000
                            val intentosUsados = nivel.intentos - intentos + 1
                            val puntosGanados = maxOf(0, base - (intentosUsados - 1) * 150 - tiempo)
                            puntuacion += puntosGanados
                            Log.d("PantallaJuego", "Puntos ganados: $puntosGanados (Total: $puntuacion)")

                            mensajeEmergente = "¡Acertaste! +$puntosGanados puntos"
                            mostrarMensajeEmergente = true

                            intentos = 0
                        } else {
                            Log.i("PantallaJuego", "FALLO - Distancia fuera del radio del nivel")
                            val direccionTexto = obtenerDireccion(latSel, lonSel, sitio.latitud, sitio.longitud)
                            Log.d("PantallaJuego", "Dirección sugerida: $direccionTexto")

                            mensajeEmergente = "Fallaste. Está más al $direccionTexto"
                            mostrarMensajeEmergente = true

                            intentos--
                        }

                        Log.d("PantallaJuego", "Intentos restantes: $intentos")

                        if (intentos == 0) {
                            Log.i("PantallaJuego", "Juego terminado - Preparando diálogo final")

                            ultimaSeleccion = Pair(latSel, lonSel)
                            ultimaDistancia = distancia

                            val distanciaStr = String.format(Locale.getDefault(), "%.2f", distancia)
                            mensajeDialogoFinal = "Tu ubicación: (${String.format(Locale.getDefault(), "%.4f", latSel)}, ${String.format(Locale.getDefault(), "%.4f", lonSel)})\n" +
                                    "Ubicación real: (${String.format(Locale.getDefault(), "%.4f", sitio.latitud)}, ${String.format(Locale.getDefault(), "%.4f", sitio.longitud)})\n\n" +
                                    "Distancia: $distanciaStr km\n\n" +
                                    "Aciertos: $aciertos / ${nivel.intentos}\n" +
                                    "Puntuación total: $puntuacion puntos"
                            mostrarDialogoFinal = true

                            Log.d("PantallaJuego", "Resultados finales: Puntos=$puntuacion, Tiempo=${tiempo}s, Aciertos=$aciertos")

                            val partida = Partida(
                                lugar = sitio,
                                nivel = nivel,
                                intentosRes = 0,
                                ayudasRes = nivel.ayuda,
                                completado = true,
                                conseguido = aciertos > 0,
                                puntuacion = puntuacion,
                                intentos = listaIntentos
                            )

                            guardarPuntuacion(
                                Puntuacion(
                                    fecha = fecha,
                                    puntuacionT = puntuacion,
                                    tiempoT = tiempo,
                                    aciertos = aciertos,
                                    historialAciertos = listOf(partida)
                                )
                            )
                            Log.d("PantallaJuego", "Puntuación guardada - Esperando a que el usuario continúe")
                        }
                    }
                )

                // Mensaje emergente superpuesto - solo mostrar si es visible
                if (mostrarMensajeEmergente) {
                    LaunchedEffect(Unit) {
                        delay(2000)
                        mostrarMensajeEmergente = false
                    }
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = mensajeEmergente,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (mensajeEmergente.startsWith("¡"))
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        if (mostrarDialogoFinal) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large,
                onDismissRequest = {},
                title = {
                    Text(
                        stringResource(R.string.resultadoFinal),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Text(
                        mensajeDialogoFinal,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
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
                        Text(stringResource(R.string.explorarMapa))
                    }
                }
            )
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
