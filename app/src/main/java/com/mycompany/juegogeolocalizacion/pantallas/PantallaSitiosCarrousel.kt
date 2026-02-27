package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.Lugar
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios
import com.mycompany.juegogeolocalizacion.navegacion.Navegador

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

    var nombreJugador by remember { mutableStateOf("") }
    var nombreGuardado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row {
                TextButton(
                    onClick = {
                        navController.navigate(Navegador.PanelPunt.ruta)
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text(
                        text = "📊",
                        fontSize = 28.sp
                    )
                }
                TextButton(
                    onClick = {
                        navController.navigate(Navegador.Records.ruta)
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text(
                        "🏆",
                        fontSize = 28.sp
                    )
                }
            }

            Row {
                TextButton(
                    onClick = {
                        navController.navigate(Navegador.Ajustes.ruta)
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text(
                        "⚙️",
                        fontSize = 28.sp
                    )
                }
                TextButton(
                    onClick = {
                        navController.navigate(Navegador.Sobre.ruta)
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text(
                        "ℹ️",
                        fontSize = 28.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "🌍 ${stringResource(R.string.selecciona_lugar)}",
            style = MaterialTheme.typography.headlineLarge
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = nombreJugador,
                    onValueChange = { nombreJugador = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.tuNombre)) },
                    singleLine = true,
                    enabled = !nombreGuardado
                )

                FilledTonalButton(
                    onClick = { nombreGuardado = true },
                    enabled = nombreJugador.isNotBlank() && !nombreGuardado
                ) {
                    Text(stringResource(R.string.confirmar))
                }
            }
        }

        Text(
            text = "Explora los destinos",
            style = MaterialTheme.typography.titleLarge
        )

        // 🔹 Carrusel grande
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            items(sitios) { sitio ->

                ElevatedCard(
                    onClick = {
                        if (nombreGuardado) {
                            onSeleccionado(sitio.id)
                        }
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.width(320.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {

                    Column {

                        Image(
                            painter = painterResource(id = sitio.imagen),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Toca para jugar",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}