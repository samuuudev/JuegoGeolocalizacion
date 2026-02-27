package com.mycompany.juegogeolocalizacion.pantallas

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.navegacion.Navegador
import com.mycompany.juegogeolocalizacion.ui.theme.BotonNivel
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSeleccionNivel(
    navController: NavController,
    onSeleccionado: (Int) -> Unit
) {

    val context = LocalContext.current

    DisposableEffect(Unit) {
        Log.d("PantallaSeleccionNivel", "Pantalla cargada")
        onDispose {
            Log.d("PantallaSeleccionNivel", "Pantalla destruida")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // ICONOS DE NAVEGACION
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row {
                TextButton(
                    onClick = {
                        CambiadorSonido.reproducirSonido(context, R.raw.boton)
                        navController.navigate(Navegador.PanelPunt.ruta)
                    },
                    modifier = Modifier.size(50.dp),
                ) {
                    Text("📊", fontSize = 28.sp)
                }
                TextButton(
                    onClick = {
                        CambiadorSonido.reproducirSonido(context, R.raw.boton)
                        navController.navigate(Navegador.Records.ruta)
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text("🏆", fontSize = 28.sp)
                }
            }

            Row {
                TextButton(
                    onClick = {
                        CambiadorSonido.reproducirSonido(context, R.raw.boton)
                        navController.navigate(Navegador.Ajustes.ruta)
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text("⚙️", fontSize = 28.sp)
                }
                TextButton(
                    onClick = {
                        CambiadorSonido.reproducirSonido(context, R.raw.boton)
                        navController.navigate(Navegador.Sobre.ruta)
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Text("ℹ️", fontSize = 28.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Image(
            painter = painterResource(R.drawable.icon),
            contentDescription = "Icon",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = stringResource(R.string.selecciona_nivel),
            style = MaterialTheme.typography.headlineSmall
        )

        // BOTONES DE NIVELES CON SONIDO
        BotonNivel(
            texto = stringResource(R.string.facil),
            colores = listOf(Color(0xFF43A047), Color(0xFF1B5E20))
        ) {
            CambiadorSonido.reproducirSonido(context, R.raw.boton)
            onSeleccionado(1)
        }

        BotonNivel(
            texto = stringResource(R.string.medio),
            colores = listOf(Color(0xFFFDD835), Color(0xFFF9A825))
        ) {
            CambiadorSonido.reproducirSonido(context, R.raw.boton)
            onSeleccionado(2)
        }

        BotonNivel(
            texto = stringResource(R.string.dificil),
            colores = listOf(Color(0xFFE53935), Color(0xFFB71C1C))
        ) {
            CambiadorSonido.reproducirSonido(context, R.raw.boton)
            onSeleccionado(3)
        }
    }
}