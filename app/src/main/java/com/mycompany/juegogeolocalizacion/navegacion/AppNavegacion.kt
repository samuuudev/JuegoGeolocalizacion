package com.mycompany.juegogeolocalizacion.navegacion

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.CambiadorIdioma
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido
import com.mycompany.juegogeolocalizacion.datos.Nivel
import com.mycompany.juegogeolocalizacion.datos.NivelActual
import com.mycompany.juegogeolocalizacion.pantallas.PantallaAjustes
import com.mycompany.juegogeolocalizacion.pantallas.PantallaJuego
import com.mycompany.juegogeolocalizacion.pantallas.PantallaPuntuaciones
import com.mycompany.juegogeolocalizacion.pantallas.PantallaRecords
import com.mycompany.juegogeolocalizacion.pantallas.PantallaSeleccionNivel
import com.mycompany.juegogeolocalizacion.pantallas.PantallaSitiosCarrousel
import com.mycompany.juegogeolocalizacion.pantallas.PantallaSobre
import com.mycompany.juegogeolocalizacion.pantallas.PantallaVideo

@Composable
fun AppNavegacion() {
    Log.d("AppNavegacion", "Inicializando sistema de navegación")
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Navegador.SeleccionNivel.ruta
    ) {
        // Parte para la seleccion del nivel
        composable(Navegador.SeleccionNivel.ruta) {
            Log.d("AppNavegacion", "Navegando a: PantallaSeleccionNivel")
            val context = LocalContext.current

            PantallaSeleccionNivel(
                navController = navController,
                onSeleccionado = { nivelId ->
                    Log.d("AppNavegacion", "Nivel seleccionado: $nivelId")
                    // Crear el nivel según la selección
                    val nivelSeleccionado = when(nivelId) {
                        1 -> Nivel(1, "Fácil", 50.0, 5, 2)
                        2 -> Nivel(2, "Medio", 25.0, 4, 1)
                        3 -> Nivel(3, "Difícil", 10.0, 3, 1)
                        else -> Nivel(2, "Medio", 25.0, 4, 1)
                    }
                    NivelActual.nivel = nivelSeleccionado
                    CambiadorSonido.reproducirSonido(context, R.raw.boton)
                    navController.navigate(Navegador.Carrousel.ruta)
                }
            )
        }

        // Parte para la seleccion de la foto en el carrousel
        composable(Navegador.Carrousel.ruta) {
            Log.d("AppNavegacion", "Navegando a: PantallaSitiosCarrousel")
            val context = LocalContext.current

            PantallaSitiosCarrousel(
                navController = navController,
                onSeleccionado = { id ->
                    Log.d("AppNavegacion", "Sitio seleccionado ID: $id")
                    CambiadorSonido.reproducirSonido(context, R.raw.boton)
                    navController.navigate(Navegador.Juego.create(id))
                }
            )
        }

        // Parte para la pantalla del juego, donde ira el mapa
        composable(Navegador.Juego.ruta) { backStackEntry ->
            val idSitio = backStackEntry.arguments?.getString("idSitio")!!.toInt()
            Log.d("AppNavegacion", "Navegando a: PantallaJuego con idSitio=$idSitio")
            PantallaJuego(idSitio, navController)
        }

        // Parte para la pantalla donde saldran las puntuaciones
        composable(Navegador.PanelPunt.ruta) {
            Log.d("AppNavegacion", "Navegando a: PantallaPuntuaciones")
            val context = LocalContext.current
            CambiadorSonido.reproducirSonido(context, R.raw.abrir_pantalla)

            PantallaPuntuaciones(context = context)
        }

        // Parte para la pantalla donde saldran los records
        composable(Navegador.Records.ruta) {
            Log.d("AppNavegacion", "Navegando a: PantallaRecords")
            val context = LocalContext.current
            CambiadorSonido.reproducirSonido(context, R.raw.abrir_pantalla)
            PantallaRecords()
        }

        // Parte para la pantalla donde saldrá el video
        composable(Navegador.Video.ruta) { backStackEntry ->
            val idSitio = backStackEntry.arguments?.getString("idSitio")!!.toInt()
            Log.d("AppNavegacion", "Navegando a: PantallaVideo con idSitio=$idSitio")
            val context = LocalContext.current
            PantallaVideo(
                idSitio,
                onVolver = {
                    Log.d("AppNavegacion", "Volviendo desde PantallaVideo")
                    CambiadorSonido.reproducirSonido(context, R.raw.boton)
                    navController.popBackStack()
                }
            )
        }


        // Parte para la pantalla de los ajustes
        composable(Navegador.Ajustes.ruta) {
            Log.d("AppNavegacion", "Navegando a: PantallaAjustes")
            val context = LocalContext.current
            CambiadorSonido.reproducirSonido(context, R.raw.abrir_pantalla)

            PantallaAjustes(
                onIdiomaChange = { idioma ->
                    Log.d("AppNavegacion", "Cambiando idioma a: $idioma")
                    CambiadorIdioma.cambiarIdioma(
                        context = navController.context,
                        codigo = idioma
                    )
                }
            )
        }

        // Parte para la pantalla de "Sobre"
        composable(Navegador.Sobre.ruta) {
            Log.d("AppNavegacion", "Navegando a: PantallaSobre")
            val context = LocalContext.current
            CambiadorSonido.reproducirSonido(context, R.raw.abrir_pantalla)

            PantallaSobre()
        }
    }
}