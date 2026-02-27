package com.mycompany.juegogeolocalizacion.pantallas

import android.net.Uri
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVideo(
    idSitio: Int,
    onVolver: () -> Unit
) {
    val sitio = ImagenesSitios.find { it.id == idSitio } ?: return
    val context = LocalContext.current

    DisposableEffect(Unit) {
        Log.d("PantallaVideo", "Pantalla cargada")
        onDispose { Log.d("PantallaVideo", "Pantalla destruida") }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(sitio.nombre) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ElevatedCard(
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        VideoView(context).apply {
                            val uri = Uri.parse("android.resource://${context.packageName}/${sitio.video}")
                            setVideoURI(uri)
                            val controller = MediaController(context)
                            controller.setAnchorView(this)
                            setMediaController(controller)
                            setOnPreparedListener { start() }
                        }
                    }
                )
            }
            FilledTonalButton(
                onClick = { onVolver() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Volver") }
        }
    }
}