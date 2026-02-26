package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.records),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(records) { record ->
                Card(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("${R.string.fecha}: ${record.fecha}")
                        Text("${R.string.puntuacionT}: ${record.puntuacionTotal}")
                        Text("${R.string.tiempoT}: ${record.tiempoTotalSegundos} segundos")
                        Text("${R.string.aciertos}: ${record.aciertos}")
                    }
                }
            }
        }
    }
}