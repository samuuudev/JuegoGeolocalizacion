package com.mycompany.juegogeolocalizacion.navegacion

sealed class Navegador(val ruta: String){
    object SeleccionNivel : Navegador("seleccion_nivel")
    object Carrousel : Navegador("carrousel")
    object Juego : Navegador("juego/{idSitio}") {
        fun create(idSitio: Int) = "juego/$idSitio"
    }
    object PanelPunt : Navegador("panel_punt")
    object Records : Navegador("records")
    object Video : Navegador("video/{idSitio}"){
        fun create(idSitio: Int) = "video/$idSitio"
    }
    object Ajustes : Navegador("ajustes")
    object Sobre : Navegador("sobre")
}