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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.datos.PuntuacionesRepo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

<<<<<<< Updated upstream
=======
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
@OptIn(ExperimentalMaterial3Api::class)
>>>>>>> Stashed changes
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

    if (ultimaPuntuacion == null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No hay puntuaciones registradas", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

<<<<<<< Updated upstream
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Resultados de la partida",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Resumen general
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Puntuación total:", fontWeight = FontWeight.Bold)
                    Text("${ultimaPuntuacion.puntuacionT} puntos", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total aciertos:", fontWeight = FontWeight.Bold)
                    Text("${ultimaPuntuacion.aciertos}", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tiempo total:", fontWeight = FontWeight.Bold)
                    Text("${ultimaPuntuacion.tiempoT} segundos", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Fecha:", fontWeight = FontWeight.Bold)
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

        // Historial de aciertos
        if (ultimaPuntuacion.historialAciertos.isNotEmpty()) {
            Text(
                text = "Detalle de aciertos:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(ultimaPuntuacion.historialAciertos) { acierto ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = acierto.nombreSitio,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Dificultad: ${acierto.dificultad}", style = MaterialTheme.typography.bodySmall)
                                Text("${acierto.puntos} pts", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Intentos: ${acierto.intentosUsados}", style = MaterialTheme.typography.bodySmall)
                                Text("Distancia: ${"%.2f".format(acierto.distanciaKm)} km", style = MaterialTheme.typography.bodySmall)
=======
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Surface(
                tonalElevation = 6.dp,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.puntuacionT),
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = "$puntuacion",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(resultados) { res ->

                    ElevatedCard(
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Image(
                                painter = painterResource(id = res.imagen),
                                contentDescription = res.nombre,
                                modifier = Modifier
                                    .height(80.dp)
                                    .width(100.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    text = res.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = if (res.acertado)
                                        stringResource(R.string.acertado)
                                    else
                                        stringResource(R.string.fallado),
                                    color = if (res.acertado)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelLarge
                                )

                                Text(
                                    text = "${stringResource(R.string.tiempo)}: ${res.tiempoSegundos}s",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "${stringResource(R.string.puntuacion)}: ${res.puntuacion}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
>>>>>>> Stashed changes
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