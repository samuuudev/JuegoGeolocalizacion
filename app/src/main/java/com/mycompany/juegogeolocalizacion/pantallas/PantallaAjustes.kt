package com.mycompany.juegogeolocalizacion.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    var sonidoActivado by remember { mutableStateOf(true) }
    var idioma by remember { mutableStateOf("ES") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(R.string.ajustes),
            style = MaterialTheme.typography.headlineMedium
        )

        // Para activar o desactivar el sonido
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.sonido))
            Switch(
                checked = sonidoActivado,
                onCheckedChange = {
                    sonidoActivado = it
                    CambiadorSonido.setSonidoActivado(it)
                }
            )
        }

        // Para seleccionar el idioma
        Column {
            Text(stringResource(R.string.idioma))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        idioma = "es"
                        onIdiomaChange("es")
                    }
                ) {
                    Text(stringResource(R.string.espaniol))
                }

                Button(
                    onClick = {
                        idioma = "en"
                        onIdiomaChange("en")
                    }
                ) {
                    Text(stringResource(R.string.ingles))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.volver))
            }
        }
    }
}