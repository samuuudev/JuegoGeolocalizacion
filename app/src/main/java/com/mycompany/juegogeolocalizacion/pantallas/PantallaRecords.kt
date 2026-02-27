package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R


data class RecordPartida(
    val fecha: String,
    val puntuacionTotal: Int,
    val tiempoTotalSegundos: Int,
    val aciertos: Int
)
val recordsEjemplo = listOf(
    RecordPartida("12/02/2026", 180, 120, 3),
    RecordPartida("10/02/2026", 150, 140, 2),
    RecordPartida("05/02/2026", 90, 200, 1)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRecords(
    records: List<RecordPartida> = recordsEjemplo
) {
    DisposableEffect(Unit) {
        Log.d("PantallaRecords", "Pantalla cargada")
        Log.d("PantallaRecords", "Total de records: ${records.size}")
        records.forEachIndexed { index, record ->
            Log.d("PantallaRecords", "  [$index] Fecha=${record.fecha}, Puntos=${record.puntuacionTotal}, Tiempo=${record.tiempoTotalSegundos}s, Aciertos=${record.aciertos}")
        }
        onDispose {
            Log.d("PantallaRecords", "Pantalla destruida")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.records),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { paddingValues ->

        val mejorPuntuacion = records.maxOfOrNull { it.puntuacionTotal }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            itemsIndexed(records.sortedByDescending { it.puntuacionTotal }) { index, record ->

                val esMejor = record.puntuacionTotal == mejorPuntuacion

                ElevatedCard(
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (esMejor)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // Cabecera con posición
                        Text(
                            text = if (esMejor) "🏆 #${index + 1}" else "#${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (esMejor)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "${stringResource(R.string.fecha)}: ${record.fecha}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "${stringResource(R.string.puntuacionT)}: ${record.puntuacionTotal}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
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