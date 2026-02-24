package com.mycompany.juegogeolocalizacion.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycompany.juegogeolocalizacion.pantallas.PantallaJuego
import com.mycompany.juegogeolocalizacion.pantallas.PantallaSeleccionNivel
import com.mycompany.juegogeolocalizacion.pantallas.PantallaSitiosCarrousel

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navegador.SeleccionNivel.ruta
    ) {
        // Parte para la seleccion del nivel
        composable(Navegador.SeleccionNivel.ruta) {
            PantallaSeleccionNivel(
                onSeleccionado = {
                    navController.navigate(Navegador.Carrousel.ruta)
                }
            )
        }

        // Parte para la seleccion de la foto en el carrousel
        composable(Navegador.Carrousel.ruta) {
            PantallaSitiosCarrousel(
                onSeleccionado = {
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
            PantallaPuntuaciones()
        }

        // Parte para la pantalla donde saldrá el video
        composable(Navegador.Video.ruta) { backStackEntry ->
            val idSitio = backStackEntry.arguments?.getString("idSitio")!!.toInt()
            PantallaVideo(idSitio)
        }

        // Parte para la pantalla de los ajustes
        composable(Navegador.Ajustes.ruta) {
            PantallaAjustes()
        }

        // Parte para la pantalla de "Sobre"
        composable(Navegador.Sobre.ruta) {
            PantallaSobre()
        }
    }
}