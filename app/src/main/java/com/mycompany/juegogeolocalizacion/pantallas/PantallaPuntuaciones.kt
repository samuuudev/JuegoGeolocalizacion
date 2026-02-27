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
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPuntuaciones() {

    Log.d("PantallaPuntuaciones", "Pantalla cargada")

    DisposableEffect(Unit) {
        onDispose {
            Log.d("PantallaPuntuaciones", "Pantalla destruida")
        }
    }

    val ultimaPuntuacion = remember {
        PuntuacionesRepo.obtenerPuntuaciones().lastOrNull()
    }

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

        if (ultimaPuntuacion == null) {

            Text(
                text = "No hay puntuaciones registradas",
                style = MaterialTheme.typography.titleMedium
            )

            return@Column
        }

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
                    text = "Resultado final",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.puntuacionT))
                    Text(
                        "${ultimaPuntuacion.puntuacionT} pts",
                        style = MaterialTheme.typography.titleMedium
                    )
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
                val fecha = try {
                    formatter.format(Date(ultimaPuntuacion.fecha.toLong()))
                } catch (_: Exception) {
                    ultimaPuntuacion.fecha
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.fecha))
                    Text(
                        fecha,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

        } else {

            Text(
                text = "No hay aciertos registrados",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}