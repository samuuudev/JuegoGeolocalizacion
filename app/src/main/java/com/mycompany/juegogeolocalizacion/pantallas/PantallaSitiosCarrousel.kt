package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.Lugar
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSitiosCarrousel(
    sitios: List<Lugar> = ImagenesSitios,
    onSeleccionado: (Int) -> Unit
) {
    DisposableEffect(Unit) {
        Log.d("PantallaSitiosCarrousel", "Pantalla cargada - ${sitios.size} sitios disponibles")
        sitios.forEachIndexed { index, sitio ->
            Log.d("PantallaSitiosCarrousel", "  [$index] ID=${sitio.id}, Nombre=${sitio.nombre}")
        }
        onDispose {
            Log.d("PantallaSitiosCarrousel", "Pantalla destruida")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.selecciona_lugar),
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
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Explora los destinos",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(sitios) { sitio ->

                    ElevatedCard(
                        onClick = {
                            Log.d(
                                "PantallaSitiosCarrousel",
                                "Usuario seleccionó sitio: ID=${sitio.id}, Nombre=${sitio.nombre}"
                            )
                            onSeleccionado(sitio.id)
                        },
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.width(280.dp)
                    ) {
                        Column {

                            Image(
                                painter = painterResource(id = sitio.imagen),
                                contentDescription = sitio.nombre,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = sitio.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Toca para jugar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}