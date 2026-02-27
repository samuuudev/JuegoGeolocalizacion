package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.PuntuacionesRepo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPuntuaciones() {
    Log.d("PantallaPuntuaciones", "Pantalla cargada")

    DisposableEffect(Unit) {
        Log.d("PantallaPuntuaciones", "PantallaPuntuaciones montada")
        onDispose {
            Log.d("PantallaPuntuaciones", "PantallaPuntuaciones desmontada")
        }
    }

    // Obtener la última puntuación guardada
    val ultimaPuntuacion = remember {
        val puntuaciones = PuntuacionesRepo.obtenerPuntuaciones()
        Log.d("PantallaPuntuaciones", "Total de puntuaciones guardadas: ${puntuaciones.size}")
        if (puntuaciones.isNotEmpty()) {
            puntuaciones.last().also {
                Log.d("PantallaPuntuaciones", "Última puntuación: ${it.puntuacionT} pts, Aciertos: ${it.aciertos}")
            }
        } else {
            null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.puntuacionT),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { paddingValues ->

        if (ultimaPuntuacion == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No hay puntuaciones registradas", style = MaterialTheme.typography.titleMedium)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Resumen general
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.puntuacionT) + ":", fontWeight = FontWeight.Bold)
                        Text(
                            "${ultimaPuntuacion.puntuacionT} puntos",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.aciertos) + ":", fontWeight = FontWeight.Bold)
                        Text(
                            "${ultimaPuntuacion.aciertos}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.tiempoT) + ":", fontWeight = FontWeight.Bold)
                        Text(
                            "${ultimaPuntuacion.tiempoT} ${stringResource(R.string.segundos)}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.fecha) + ":", fontWeight = FontWeight.Bold)
                        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val fecha = try {
                            formatter.format(Date(ultimaPuntuacion.fecha.toLong()))
                        } catch (_: Exception) {
                            ultimaPuntuacion.fecha
                        }
                        Text(fecha, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Historial de aciertos (usando Partida)
            if (ultimaPuntuacion.historialAciertos.isNotEmpty()) {
                Text(
                    text = "Detalle de aciertos:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(ultimaPuntuacion.historialAciertos) { partida ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = partida.lugar.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Dificultad: ${partida.nivel.nombre}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "${partida.puntuacion} pts",
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Intentos: ${partida.intentos.size}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    val dist = partida.intentos.lastOrNull()?.distanciaKm ?: 0.0
                                    Text(
                                        "Distancia: ${"%.2f".format(dist)} km",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text("No hay aciertos registrados", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

