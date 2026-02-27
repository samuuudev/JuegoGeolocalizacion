package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.PuntuacionesRepo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPuntuaciones() {

    Log.d("PantallaPuntuaciones", "Pantalla cargada")

    DisposableEffect(Unit) {
        onDispose {
            Log.d("PantallaPuntuaciones", "Pantalla destruida")
        }
    }

    val ultimaEntrada = remember {
        PuntuacionesRepo.obtenerPuntuaciones().lastOrNull()
    }

    if (ultimaEntrada == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.noPunt),
                style = MaterialTheme.typography.titleMedium
            )
        }
        return
    }

    val nombreJugador = ultimaEntrada.first
    val ultimaPuntuacion = ultimaEntrada.second

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📊 ${stringResource(R.string.puntuacionT)}",
            style = MaterialTheme.typography.headlineLarge
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = stringResource(R.string.resultadoFinal),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Jugador: $nombreJugador",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.puntuacionT))
                    Text("${ultimaPuntuacion.puntuacionT} pts")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.aciertos))
                    Text("${ultimaPuntuacion.aciertos}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.tiempoT))
                    Text("${ultimaPuntuacion.tiempoT} ${stringResource(R.string.segundos)}")
                }

                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                val fechaFormateada = try {
                    formatter.format(Date(ultimaPuntuacion.fecha.toLong()))
                } catch (e: Exception) {
                    ultimaPuntuacion.fecha
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.fecha))
                    Text(fechaFormateada)
                }
            }
        }

        if (ultimaPuntuacion.historialAciertos.isNotEmpty()) {

            Text(
                text = "📍 Detalle de aciertos",
                style = MaterialTheme.typography.titleLarge
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                items(ultimaPuntuacion.historialAciertos) { partida ->

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = partida.lugar.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Dificultad: ${partida.nivel.nombre}")
                                Text("${partida.puntuacion} pts")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Intentos: ${partida.intentos.size}")

                                val dist = partida.intentos.lastOrNull()?.distanciaKm ?: 0.0
                                Text("Distancia: ${"%.2f".format(dist)} km")
                            }
                        }
                    }
                }
            }
        }
    }
}