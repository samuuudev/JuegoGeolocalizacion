package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R

@Composable
fun PantallaSeleccionNivel(
    onSeleccionado: (Int) -> Unit
) {
    DisposableEffect(Unit) {
        Log.d("PantallaSeleccionNivel", "Pantalla cargada")
        onDispose {
            Log.d("PantallaSeleccionNivel", "Pantalla destruida")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titulo
        Text(
            text = stringResource(R.string.selecciona_nivel),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Boton para seleccionar el tipo de juego facil
        Button(
            onClick = {
                Log.d("PantallaSeleccionNivel", "Usuario seleccionó nivel: Fácil (1)")
                onSeleccionado(1)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.facil))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Boton para seleccionar el tipo de juego medio
        Button(
            onClick = {
                Log.d("PantallaSeleccionNivel", "Usuario seleccionó nivel: Medio (2)")
                onSeleccionado(2)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.medio))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Boton para seleccionar el tipo de juego dificil
        Button(
            onClick = {
                Log.d("PantallaSeleccionNivel", "Usuario seleccionó nivel: Difícil (3)")
                onSeleccionado(3)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.dificil))
        }
    }
}
