package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.PuntuacionesRepo
import com.mycompany.juegogeolocalizacion.datos.Record

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRecords() {

    DisposableEffect(Unit) {
        Log.d("PantallaRecords", "Pantalla cargada")
        onDispose {
            Log.d("PantallaRecords", "Pantalla destruida")
        }
    }

    // Convertir las puntuaciones guardadas en records
    val records: List<Record> = PuntuacionesRepo.obtenerPuntuaciones().map { (nombre, puntuacion) ->
        Record(
            nombreJugador = nombre,
            fecha = puntuacion.fecha,
            puntuacionTotal = puntuacion.puntuacionT,
            tiempoTotalSegundos = puntuacion.tiempoT,
            aciertos = puntuacion.aciertos
        )
    }

    val recordsOrdenados = records.sortedByDescending { it.puntuacionTotal }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "🏆 ${stringResource(R.string.records)}",
            style = MaterialTheme.typography.headlineLarge
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            itemsIndexed(recordsOrdenados) { index, record ->

                val medalla = when (index) {
                    0 -> "🥇"
                    1 -> "🥈"
                    2 -> "🥉"
                    else -> "${index + 1}."
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Text(
                                text = medalla,
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Text(
                                text = record.nombreJugador,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = "${stringResource(R.string.puntuacionT)}: ${record.puntuacionTotal}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "${stringResource(R.string.fecha)}: ${record.fecha}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "${stringResource(R.string.tiempoT)}: ${record.tiempoTotalSegundos}s",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "${stringResource(R.string.aciertos)}: ${record.aciertos}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}