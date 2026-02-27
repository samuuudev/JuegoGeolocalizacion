package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.EstadoJuego
import com.mycompany.juegogeolocalizacion.datos.Lugar
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSitiosCarrousel(
    navController: NavController,
    sitios: List<Lugar> = ImagenesSitios,
    onSeleccionado: (Int) -> Unit
) {

    DisposableEffect(Unit) {
        Log.d("PantallaSitiosCarrousel", "Pantalla cargada")
        onDispose {
            Log.d("PantallaSitiosCarrousel", "Pantalla destruida")
        }
    }

    val nombreGuardado = EstadoJuego.nombreJugador.value.isNotBlank()
    var nombreLocal by remember { mutableStateOf(EstadoJuego.nombreJugador.value) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "🌍 ${stringResource(R.string.selecciona_lugar)}",
            style = MaterialTheme.typography.headlineLarge
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = nombreLocal,
                    onValueChange = { nombreLocal = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.tuNombre)) },
                    singleLine = true,
                    enabled = !nombreGuardado
                )

                FilledTonalButton(
                    onClick = {
                        EstadoJuego.nombreJugador.value = nombreLocal
                    },
                    enabled = nombreLocal.isNotBlank() && !nombreGuardado
                ) {
                    Text(stringResource(R.string.confirmar))
                }
            }
        }

        Text(
            text = "Explora los destinos",
            style = MaterialTheme.typography.titleLarge
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            items(sitios) { sitio ->

                val resultado = EstadoJuego.resultadoSitio[sitio.id]

                ElevatedCard(
                    onClick = {
                        if (nombreGuardado) {
                            onSeleccionado(sitio.id)
                        }
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.width(320.dp)
                ) {

                    Box {

                        Image(
                            painter = painterResource(id = sitio.imagen),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            contentScale = ContentScale.Crop
                        )

                        // Overlay si acertado/fallado
                        if (resultado != null) {
                            Surface(
                                modifier = Modifier
                                    .matchParentSize(),
                                color = if (resultado)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                else
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.35f)
                            ) {}
                        }

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .padding(20.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = stringResource(R.string.tocar),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}