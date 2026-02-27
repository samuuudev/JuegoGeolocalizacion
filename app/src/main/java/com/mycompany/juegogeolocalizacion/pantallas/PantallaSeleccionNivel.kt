package com.mycompany.juegogeolocalizacion.pantallas

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mycompany.juegogeolocalizacion.R

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PantallaSeleccionNivel(
                onSeleccionado = { nivel ->
                    Log.d("MainActivity", "Nivel seleccionado: $nivel")
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    Row {
                        TextButton(onClick = { }) { Text("📊") }
                        TextButton(onClick = { }) { Text("🏆") }
                    }
                },
                actions = {
                    TextButton(onClick = { }) { Text("⚙️") }
                    TextButton(onClick = { }) { Text("ℹ️") }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = stringResource(R.string.selecciona_nivel),
                style = MaterialTheme.typography.headlineSmall
            )

            Button(
                onClick = { onSeleccionado(1) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.facil))
            }

            Button(
                onClick = { onSeleccionado(2) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF9A825),
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.medio))
            }

            Button(
                onClick = { onSeleccionado(3) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC62828),
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.dificil))
            }
        }
    }
}