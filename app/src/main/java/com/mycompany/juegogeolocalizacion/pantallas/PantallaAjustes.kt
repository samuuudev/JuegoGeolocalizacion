package com.mycompany.juegogeolocalizacion.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido

@Composable
fun PantallaAjustes(
    onIdiomaChange: (String) -> Unit = {}
) {
    var sonidoActivado by remember { mutableStateOf(true) }
    var idioma by remember { mutableStateOf("ES") }
    var context = LocalContext.current


    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "⚙️ ${stringResource(R.string.ajustes)}",
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
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🔊 ${stringResource(R.string.sonido)}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (sonidoActivado) stringResource(R.string.activado) else stringResource(R.string.desactivado),
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

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "🌍 ${stringResource(R.string.idioma)}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FilledTonalButton(
                        onClick = {
                            CambiadorSonido.reproducirSonido(context, R.raw.boton)
                            idioma = "ES"
                            onIdiomaChange("es")
                        },
                        enabled = idioma != "ES"
                    ) {
                        Text("🇪🇸 ${stringResource(R.string.espaniol)}")
                    }
                    FilledTonalButton(
                        onClick = {
                            CambiadorSonido.reproducirSonido(context, R.raw.boton)
                            idioma = "EN"
                            onIdiomaChange("en")
                        },
                        enabled = idioma != "EN"
                    ) {
                        Text("🇬🇧 ${stringResource(R.string.ingles)}")
                    }
                }
            }
        }
    }
}