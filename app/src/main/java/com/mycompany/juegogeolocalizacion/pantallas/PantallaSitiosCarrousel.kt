package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.EstadoJuego
import com.mycompany.juegogeolocalizacion.datos.Lugar
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido
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

    val context = LocalContext.current

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
                        CambiadorSonido.reproducirSonido(context, R.raw.boton)
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
                            CambiadorSonido.reproducirSonido(context, R.raw.boton)
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
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                else
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
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