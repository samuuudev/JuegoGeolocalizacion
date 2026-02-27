package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido

@Composable
fun PantallaAjustes(
    onIdiomaChange: (String) -> Unit = {},
    onVolver: () -> Unit = {}
){
    DisposableEffect(Unit) {
        Log.d("PantallaAjustes", "Pantalla cargada")
        onDispose {
            Log.d("PantallaAjustes", "Pantalla destruida")
        }
    }

    var sonidoActivado by remember { mutableStateOf(true) }
    var idioma by remember { mutableStateOf("ES") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = stringResource(R.string.ajustes),
            style = MaterialTheme.typography.headlineMedium
        )

        // Sonido
        Surface(
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.sonido),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (sonidoActivado) "Activado" else "Desactivado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = sonidoActivado,
                    onCheckedChange = {
                        sonidoActivado = it
                        CambiadorSonido.setSonidoActivado(it)
                    }
                )
            }
        }

        // Idioma
        Surface(
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.idioma),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    FilledTonalButton(
                        onClick = {
                            idioma = "ES"
                            onIdiomaChange("es")
                        },
                        enabled = idioma != "ES"
                    ) {
                        Text(stringResource(R.string.espaniol))
                    }

                    FilledTonalButton(
                        onClick = {
                            idioma = "EN"
                            onIdiomaChange("en")
                        },
                        enabled = idioma != "EN"
                    ) {
                        Text(stringResource(R.string.ingles))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ElevatedButton(
            onClick = { onVolver() },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.volver))
        }
    }
}