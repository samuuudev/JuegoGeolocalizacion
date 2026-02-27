package com.mycompany.juegogeolocalizacion.pantallas

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.datos.PuntuacionesRepo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPuntuaciones(context: Context) {

    LaunchedEffect(Unit) {
        PuntuacionesRepo.inicializar(context)
    }

    val puntuaciones = remember { PuntuacionesRepo.obtenerPuntuaciones() }
    val ultimaEntrada = puntuaciones.firstOrNull()

    if (ultimaEntrada == null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No hay puntuaciones registradas", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    val nombreJugador = ultimaEntrada.first
    val ultimaPuntuacion = ultimaEntrada.second

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("📊 Puntuaciones", style = MaterialTheme.typography.headlineLarge)

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Resultado final", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text("Jugador: $nombreJugador", style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Puntuación")
                    Text("${ultimaPuntuacion.puntuacionT} pts")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Aciertos")
                    Text("${ultimaPuntuacion.aciertos}")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tiempo")
                    Text("${ultimaPuntuacion.tiempoT} s")
                }
                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val fechaFormateada = try { formatter.format(Date(ultimaPuntuacion.fecha.toLong())) } catch (e: Exception) { ultimaPuntuacion.fecha }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Fecha")
                    Text(fechaFormateada)
                }
            }
        }

        if (ultimaPuntuacion.historialAciertos.isNotEmpty()) {
            Text("📍 Detalle de aciertos", style = MaterialTheme.typography.titleLarge)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                items(ultimaPuntuacion.historialAciertos) { partida ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(partida.lugar.nombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Dificultad: ${partida.nivel.nombre}")
                                Text("${partida.puntuacion} pts")
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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