package com.mycompany.juegogeolocalizacion.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R

@Composable
fun PantallaSeleccionNivel(
    onSeleccionado: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment  = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.selecciona_nivel),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(21.dp))

        Button(
            onClick = { onSeleccionado(1) },
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.facil))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSeleccionado(2) },
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.medio))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSeleccionado(3) },
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.dificil))
        }
    }
}