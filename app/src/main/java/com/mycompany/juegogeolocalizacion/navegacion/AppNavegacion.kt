package com.mycompany.juegogeolocalizacion.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycompany.juegogeolocalizacion.R
import com.mycompany.juegogeolocalizacion.datos.CambiadorIdioma
import com.mycompany.juegogeolocalizacion.datos.CambiadorSonido
import com.mycompany.juegogeolocalizacion.datos.NivelActual
import com.mycompany.juegogeolocalizacion.datos.NivelActual.nivel
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
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navegador.SeleccionNivel.ruta
    ) {
        // Parte para la seleccion del nivel
        composable(Navegador.SeleccionNivel.ruta) {
            val context = LocalContext.current

            PantallaSeleccionNivel(
                onSeleccionado = {
                    NivelActual.nivel = nivel
                    CambiadorSonido.repoducirSonido(context, R.raw.boton)
                    navController.navigate(Navegador.Carrousel.ruta)
                }
            )
        }

        // Parte para la seleccion de la foto en el carrousel
        composable(Navegador.Carrousel.ruta) {
            val context = LocalContext.current

            PantallaSitiosCarrousel(
                onSeleccionado = { id ->
                    CambiadorSonido.repoducirSonido(context, R.raw.boton)
                    navController.navigate(Navegador.Juego.create(id))
                }
            )
        }

        // Parte para la pantalla del juego, donde ira el mapa
        composable(Navegador.Juego.ruta) { backStackEntry ->
            val idSitio = backStackEntry.arguments?.getString("idSitio")!!.toInt()
            PantallaJuego(idSitio)
        }

        // Parte para la pantalla donde saldran las puntuaciones
        composable(Navegador.PanelPunt.ruta) {
            val context = LocalContext.current
            CambiadorSonido.repoducirSonido(context, R.raw.abrir_pantalla)

            PantallaPuntuaciones()
        }

        // Parte para la pantalla donde saldran los records
        composable(Navegador.Records.ruta) {
            val context = LocalContext.current
            CambiadorSonido.repoducirSonido(context, R.raw.abrir_pantalla)
            PantallaRecords()
        }

        // Parte para la pantalla donde saldrá el video
        composable(Navegador.Video.ruta) { backStackEntry ->
            val idSitio = backStackEntry.arguments?.getString("idSitio")!!.toInt()
            PantallaVideo(
                idSitio,
                onVolver = {
                    CambiadorSonido.repoducirSonido(LocalContext.current, R.raw.boton)
                    navController.popBackStack()
                }
            )
        }

        // Parte para la pantalla de los ajustes
        composable(Navegador.Ajustes.ruta) {
            val context = LocalContext.current
            CambiadorSonido.repoducirSonido(context, R.raw.abrir_pantalla)

            PantallaAjustes(
                onIdiomaChange = { idioma ->
                    CambiadorIdioma.cambiarIdioma(
                        context = navController.context,
                        codigo = idioma
                    )
                },
                onVolver = {
                    CambiadorSonido.repoducirSonido(context, R.raw.boton)
                    navController.popBackStack()
                }
            )
        }

        // Parte para la pantalla de "Sobre"
        composable(Navegador.Sobre.ruta) {
            val context = LocalContext.current
            CambiadorSonido.repoducirSonido(context, R.raw.abrir_pantalla)

            PantallaSobre(
                onVolver = {
                    CambiadorSonido.repoducirSonido(context, R.raw.boton)
                    navController.popBackStack()
                }
            )
        }
    }
}