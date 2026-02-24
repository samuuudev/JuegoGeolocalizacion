package com.mycompany.juegogeolocalizacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.Lugar
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios

@Composable
fun PantallaSitiosCarrousel(
    sitios: List<Lugar> = ImagenesSitios,
    onSeleccionado: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.selecciona_lugar),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(21.dp))

        LazyRow(
            horizontalArrangement  = Arrangement.spacedBy(16.dp)
        ) {
            items(sitios) { sitio ->
                Card(
                    modifier = Modifier.width(250.dp).clickable { onSeleccionado(sitio.id) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = sitio.imagen),
                            contentDescription = sitio.nombre,
                            modifier = Modifier.fillMaxSize().height(150.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(sitio.nombre)

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}