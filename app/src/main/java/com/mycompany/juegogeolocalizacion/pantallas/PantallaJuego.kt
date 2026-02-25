package com.mycompany.juegogeolocalizacion.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.navArgument
import com.mycompany.juegogeolocalizacion.modelo.ImagenesSitios
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido
import com.mycompany.juegogeolocalizacion.datos.NivelActual
import com.mycompany.juegogeolocalizacion.datos.Puntuacion
import com.mycompany.juegogeolocalizacion.datos.PuntuacionesRepo.guardarPuntuacion
import com.mycompany.juegogeolocalizacion.navegacion.Navegador
import kotlinx.coroutines.delay

@Composable
fun PantallaJuego(
    idSitio: Int,
    navController: NavController
) {
    val sitio = ImagenesSitios.find { it.id == idSitio } ?: return
    val nivel = NivelActual.nivel ?: return
    val fecha = System.currentTimeMillis().toString()
    val puntuacion by remember { mutableStateOf(0) }
    var intentos by remember { mutableStateOf(nivel.intentos) }
    var aciertos by remember { mutableStateOf(0) }
    var tiempo by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // Creamos un temporizador
    LaunchedEffect(true) {
        while(true) {
            delay(1000)
            tiempo ++
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Imagen del sitio a adivinar
        Image(
            painter = painterResource(id = sitio.imagen),
            contentDescription = sitio.nombre,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Intentos restantes que quedan
        Text(
            text = "${R.string.intentos_res}: ${intentos}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Boton para la ayuda con IA
        Button(
            onClick = {
                CambiadorSonido.repoducirSonido(context, R.raw.boton)
                // Falta la lógica de la ayuda con IA
            }
        ) {
            Text(stringResource(R.string.ayuda))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mapa con el AndroidView
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            MapOSM(
                sitio = sitio,
                onMapTap = {
                    CambiadorSonido.repoducirSonido(context, R.raw.tocar_mapa)
                    if(intentos > 0) {
                        intentos--

                        // Falta por conectar con la distancia
                        if(intentos == nivel.intentos - 1) {
                            aciertos ++
                        }

                        if(intentos == 0){
                            guardarPuntuacion(
                                Puntuacion(
                                    fecha = fecha,
                                    puntuacionT = puntuacion,
                                    tiempoT = tiempo,
                                    aciertos = aciertos
                                )
                            )

                            navController.navigate(Navegador.PanelPunt.ruta)
                        }
                    } else {
                        // Mostrar mensaje de fin de juego
                    }
                }
            )
        }
    }
}