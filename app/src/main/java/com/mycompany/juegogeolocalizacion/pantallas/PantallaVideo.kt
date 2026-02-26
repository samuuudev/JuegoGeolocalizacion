package com.mycompany.juegogeolocalizacion.pantallas

import android.net.Uri
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios
import com.mycompany.juegogeolocalizacion.R


@Composable
fun PantallaVideo(
    idSitio: Int,
    onVolver: () -> Unit
){
    Log.d("PantallaVideo", "Cargando video para idSitio=$idSitio")

    val sitio = ImagenesSitios.find { it.id == idSitio }
    if (sitio == null) {
        Log.e("PantallaVideo", "ERROR: No se encontró sitio con ID=$idSitio")
        return
    }
    Log.d("PantallaVideo", "Sitio encontrado: ${sitio.nombre}, Video ID=${sitio.video}")

    val context = LocalContext.current

    DisposableEffect(Unit) {
        Log.d("PantallaVideo", "Pantalla cargada - Reproduciendo video")
        onDispose {
            Log.d("PantallaVideo", "Pantalla destruida - Deteniendo video")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = sitio.nombre,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            modifier = Modifier.fillMaxSize().weight(1f),
            factory = {
                Log.d("PantallaVideo", "Creando VideoView")
                VideoView(context).apply {
                    val uri = Uri.parse("android.resource://${context.packageName}/${sitio.video}")
                    Log.d("PantallaVideo", "URI del video: $uri")
                    setVideoURI(uri)

                    val controller = MediaController(context)
                    controller.setAnchorView(this)
                    setMediaController(controller)

                    setOnPreparedListener { mp ->
                        Log.d("PantallaVideo", "Video preparado - Duración: ${mp.duration}ms")
                        start()
                    }

                    setOnCompletionListener {
                        Log.d("PantallaVideo", "Video completado")
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.e("PantallaVideo", "ERROR reproduciendo video: what=$what, extra=$extra")
                        true
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Log.d("PantallaVideo", "Usuario presionó botón Volver")
                onVolver()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.volver))
        }
    }

}