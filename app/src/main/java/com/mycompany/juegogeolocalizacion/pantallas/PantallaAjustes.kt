package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
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
                    Log.d("PantallaAjustes", "Cambio sonido: $sonidoActivado -> $it")
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        Log.d("PantallaAjustes", "Cambiando idioma a: ES")
                        idioma = "ES"
                        onIdiomaChange("es")
                    }
                ) {
                    Text("Español")
                }

                Button(
                    onClick = {
                        Log.d("PantallaAjustes", "Cambiando idioma a: EN")
                        idioma = "EN"
                        onIdiomaChange("en")
                    }
                ) {
                    Text("English")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                Log.d("PantallaAjustes", "Usuario presionó Volver")
                onVolver()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.volver))
        }
    }
}