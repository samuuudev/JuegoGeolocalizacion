package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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

    // Lista para registrar detalles de cada acierto
    var acieertos by remember { mutableStateOf<List<com.mycompany.juegogeolocalizacion.datos.AciertoDetalle>>(emptyList()) }
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
                contentAlignment = Alignment.Center
            ) {
                // Mensaje emergente en la parte superior del mapa
                if (mostrarMensajeEmergente) {
                    LaunchedEffect(Unit) {
                        delay(2000)
                        mostrarMensajeEmergente = false
                    }

<<<<<<< Updated upstream
                    val distancia = calcularDistanciaKm(
                        latSel, lonSel,
                        sitio.latitud, sitio.longitud
                    )
                    Log.d("PantallaJuego", "Distancia calculada: ${"%.2f".format(distancia)} km")

                    // Radio fijo por nivel
                    val radioNivel = when (nivel.id) {
                        1 -> 50.0  // Nivel Fácil: 50 km
                        2 -> 25.0  // Nivel Medio: 25 km
                        3 -> 10.0  // Nivel Difícil: 10 km
                        else -> 25.0
                    }
                    Log.d("PantallaJuego", "Radio del nivel (${nivel.nombre}): $radioNivel km")

                    // Decidir acierto usando el radio FIJO del nivel
                    val esAcierto = distancia <= radioNivel
                    if (esAcierto) {
                        Log.i("PantallaJuego", "¡ACIERTO! Distancia dentro del radio del nivel")
                        aciertos++
                        val base = 1000
                        val intentosUsados = nivel.intentos - intentos + 1
                        val puntosGanados = maxOf(0, base - (intentosUsados - 1) * 150 - tiempo)
                        puntuacion += puntosGanados
                        Log.d("PantallaJuego", "Puntos ganados: $puntosGanados (Total: $puntuacion)")

                        // Registrar detalle del acierto
                        val detalle = com.mycompany.juegogeolocalizacion.datos.AciertoDetalle(
                            nombreSitio = sitio.nombre,
                            dificultad = nivel.nombre,
                            puntos = puntosGanados,
                            intentosUsados = intentosUsados,
                            distanciaKm = distancia
                        )
                        acieertos = acieertos + detalle
                        Log.d("PantallaJuego", "Acierto registrado: ${detalle.nombreSitio} - ${detalle.puntos} puntos")

                        // Mostrar mensaje emergente corto
                        mensajeEmergente = "✓ ¡Acertaste! +$puntosGanados puntos"
                        mostrarMensajeEmergente = true

                        Log.i("PantallaJuego", "¡ACIERTO! Terminando juego inmediatamente")
                        intentos = 0
                    } else {
                        Log.i("PantallaJuego", "FALLO - Distancia fuera del radio del nivel")
                        val direccion = obtenerDireccion(latSel, lonSel, sitio.latitud, sitio.longitud)
                        Log.d("PantallaJuego", "Dirección sugerida: $direccion")

                        // Mostrar mensaje emergente corto
                        mensajeEmergente = "✗ Fallaste. Está más al $direccion"
                        mostrarMensajeEmergente = true

                        intentos--
                    }

                    Log.d("PantallaJuego", "Intentos restantes: $intentos")

                    // Si se agotaron los intentos, mostrar diálogo final con línea y localización
                    if (intentos == 0) {
                        Log.i("PantallaJuego", "Juego terminado - Preparando diálogo final")

                        // Guardar selección y distancia SOLO para mostrar en diálogo final
                        ultimaSeleccion = Pair(latSel, lonSel)
                        ultimaDistancia = distancia

                        // Preparar mensaje final simple (solo info del último acierto)
                        val distanciaStr = String.format(Locale.getDefault(), "%.2f", distancia)

                        mensajeDialogoFinal = "Tu ubicación: (${String.format(Locale.getDefault(), "%.4f", latSel)}, ${String.format(Locale.getDefault(), "%.4f", lonSel)})\n" +
                                "Ubicación real: (${String.format(Locale.getDefault(), "%.4f", sitio.latitud)}, ${String.format(Locale.getDefault(), "%.4f", sitio.longitud)})\n\n" +
                                "Distancia: $distanciaStr km\n\n" +
                                "Puntuación total: $puntuacion puntos"
                        mostrarDialogoFinal = true

                        Log.d("PantallaJuego", "Resultados finales: Puntos=$puntuacion, Tiempo=${tiempo}s, Aciertos=$aciertos")

                        guardarPuntuacion(
                            Puntuacion(
                                fecha = fecha,
                                puntuacionT = puntuacion,
                                tiempoT = tiempo,
                                aciertos = aciertos,
                                historialAciertos = acieertos
=======
                    this@Column.AnimatedVisibility(visible = mostrarMensajeEmergente) {
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
>>>>>>> Stashed changes
                            )
                        }
                    }
                }

                MapOSM(
                    sitio = sitio,
                    targetPoint = if (ultimaSeleccion != null) Pair(
                        sitio.latitud,
                        sitio.longitud
                    ) else null,
                    distanciaKm = ultimaDistancia,
                    onSelectionConfirmed = { latSel, lonSel, radiusKm ->
                        Log.d(
                            "PantallaJuego",
                            "Selección confirmada: lat=$latSel, lon=$lonSel, radioKm=$radiusKm"
                        )
                        CambiadorSonido.repoducirSonido(context, R.raw.tocar_mapa)

                        if (intentos <= 0) {
                            Log.w("PantallaJuego", "No quedan intentos - Selección ignorada")
                            return@MapOSM
                        }

                        val distancia = calcularDistanciaKm(
                            latSel, lonSel,
                            sitio.latitud, sitio.longitud
                        )
                        Log.d(
                            "PantallaJuego",
                            "Distancia calculada: ${"%.2f".format(distancia)} km"
                        )

                        // Radio fijo por nivel
                        val radioNivel = when (nivel.id) {
                            1 -> 50.0  // Nivel Fácil: 50 km
                            2 -> 25.0  // Nivel Medio: 25 km
                            3 -> 10.0  // Nivel Difícil: 10 km
                            else -> 25.0
                        }
                        Log.d("PantallaJuego", "Radio del nivel (${nivel.nombre}): $radioNivel km")

                        // Decidir acierto usando el radio FIJO del nivel
                        val esAcierto = distancia <= radioNivel
                        if (esAcierto) {
                            Log.i("PantallaJuego", "¡ACIERTO! Distancia dentro del radio del nivel")
                            aciertos++
                            val base = 1000
                            val intentosUsados = nivel.intentos - intentos + 1
                            val puntosGanados = maxOf(0, base - (intentosUsados - 1) * 150 - tiempo)
                            puntuacion += puntosGanados
                            Log.d(
                                "PantallaJuego",
                                "Puntos ganados: $puntosGanados (Total: $puntuacion)"
                            )

                            // Mostrar mensaje emergente corto
                            mensajeEmergente = "¡Acertaste! +$puntosGanados puntos"
                            mostrarMensajeEmergente = true

                            Log.i("PantallaJuego", "¡ACIERTO! Terminando juego inmediatamente")
                            intentos = 0
                        } else {
                            Log.i("PantallaJuego", "FALLO - Distancia fuera del radio del nivel")
                            val direccion =
                                obtenerDireccion(latSel, lonSel, sitio.latitud, sitio.longitud)
                            Log.d("PantallaJuego", "Dirección sugerida: $direccion")

                            // Mostrar mensaje emergente corto
                            mensajeEmergente = "Fallaste. Está más al $direccion"
                            mostrarMensajeEmergente = true

                            intentos--
                        }

                        Log.d("PantallaJuego", "Intentos restantes: $intentos")

                        // Si se agotaron los intentos, mostrar diálogo final con línea y localización
                        if (intentos == 0) {
                            Log.i("PantallaJuego", "Juego terminado - Preparando diálogo final")

                            // Guardar selección y distancia SOLO para mostrar en diálogo final
                            ultimaSeleccion = Pair(latSel, lonSel)
                            ultimaDistancia = distancia

                            // Preparar mensaje final detallado
                            val distanciaStr = String.format(Locale.getDefault(), "%.2f", distancia)
                            mensajeDialogoFinal = "Tu ubicación: (${
                                String.format(
                                    Locale.getDefault(),
                                    "%.4f",
                                    latSel
                                )
                            }, ${String.format(Locale.getDefault(), "%.4f", lonSel)})\n" +
                                    "Ubicación real: (${
                                        String.format(
                                            Locale.getDefault(),
                                            "%.4f",
                                            sitio.latitud
                                        )
                                    }, ${
                                        String.format(
                                            Locale.getDefault(),
                                            "%.4f",
                                            sitio.longitud
                                        )
                                    })\n\n" +
                                    "Distancia: $distanciaStr km\n\n" +
                                    "Aciertos: $aciertos / ${nivel.intentos}\n" +
                                    "Puntuación total: $puntuacion puntos"
                            mostrarDialogoFinal = true

                            Log.d(
                                "PantallaJuego",
                                "Resultados finales: Puntos=$puntuacion, Tiempo=${tiempo}s, Aciertos=$aciertos"
                            )

                            guardarPuntuacion(
                                Puntuacion(
                                    fecha = fecha,
                                    puntuacionT = puntuacion,
                                    tiempoT = tiempo,
                                    aciertos = aciertos
                                )
                            )
                            Log.d(
                                "PantallaJuego",
                                "Puntuación guardada - Esperando a que el usuario continúe"
                            )
                        }
                    }
                )
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