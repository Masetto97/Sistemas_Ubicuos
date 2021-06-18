package com.example.practica_final;
/**
 * Clase donde se guardan todas las variables relativas al sistema y todos los Activity pueden acceder a ella
 *
 * @author Eduardo Mora González
 */
public class GlobalInfo {

    //Variable para saber si se quiere o no el guardado en la BBDD
    public static boolean activoBBDD = true;

    //Variable para saber si se quiere o no el mapa de GoogleMap
    public static boolean activoMapaGoogle = true;

    //Variable para saber el host que se busca
    public static String url = "www.ubu.es";

    //Variable para saber si se quiere o no de mapa
    public static boolean cambioMapa = false;

    //Variable para saber si ha ocurrido o no un error durante la ejeccución del proyecto
    public static boolean error = false;

}
