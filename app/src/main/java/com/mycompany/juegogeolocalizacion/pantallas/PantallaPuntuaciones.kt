package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R

data class ResultadosLugar(
    val id: Int,
    val nombre: String,
    val imagen: Int,
    val tiempoSegundos: Int,
    val puntuacion: Int,
    val acertado: Boolean
)
val resultadosEjemplo = listOf(
    ResultadosLugar(1, "Plaza Mayor", R.drawable.plaza, 32, 70, true),
    ResultadosLugar(2, "La Giralda", R.drawable.coliseo, 55, 30, false) )
@Composable
fun PantallaPuntuaciones(
    resultados: List<ResultadosLugar> = resultadosEjemplo
) {
    DisposableEffect(Unit) {
        Log.d("PantallaPuntuaciones", "Pantalla cargada")
        Log.d("PantallaPuntuaciones", "Total de resultados: ${resultados.size}")
        resultados.forEach { resultado ->
            Log.d("PantallaPuntuaciones", "  ${resultado.nombre}: ${resultado.puntuacion} pts, Tiempo=${resultado.tiempoSegundos}s, Acertado=${resultado.acertado}")
        }
        onDispose {
            Log.d("PantallaPuntuaciones", "Pantalla destruida")
        }
    }

    val puntuacion = resultados.sumOf { it.puntuacion }
    Log.d("PantallaPuntuaciones", "Puntuación total calculada: $puntuacion")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Puntuacion total
        Text(
            text = ("${R.string.puntuacionT}: ${puntuacion}"),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(21.dp))

        // Lista con los resultados
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(resultados) { res ->
                Card(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = res.imagen),
                            contentDescription = res.nombre,
                            modifier = Modifier.height(80.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = res.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text =
                                    if(res.acertado) {
                                        stringResource(R.string.acertado)
                                    } else {
                                        stringResource(R.string.fallado)
                                    }
                                ,
                                color =
                                    if(res.acertado) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    }
                            )

                            Text("${R.string.tiempo}: ${res.tiempoSegundos} segundos")
                            Text("${R.string.puntuacion}: ${res.tiempoSegundos} segundos")
                        }
                    }
                }
            }
        }
    }
}