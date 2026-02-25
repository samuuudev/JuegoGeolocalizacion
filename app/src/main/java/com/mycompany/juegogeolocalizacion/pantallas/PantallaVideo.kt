package com.mycompany.juegogeolocalizacion.pantallas

import android.net.Uri
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
    onVolver: @Composable () -> Unit
){
    val sitio = ImagenesSitios.find { it.id == idSitio } ?: return
    val context = LocalContext.current

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
                VideoView(context).apply {
                    val uri = Uri.parse("android.resource://${context.packageName}/${sitio.video}")
                    setVideoURI(uri)

                    val controller = MediaController(context)
                    controller.setAnchorView(this)
                    setMediaController(controller)

                    start()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.volver))
        }
    }

}