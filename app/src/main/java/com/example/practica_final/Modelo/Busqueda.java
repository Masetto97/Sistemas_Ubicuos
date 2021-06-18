package com.example.practica_final.Modelo;

import java.util.Date;
/**
 * Clase donde se muestran lo relativo a una busqueda
 *
 * @author Eduardo Mora González
 */
public class Busqueda {

    //Parametros de la Busqueda
    private static int Id;
    private String link;
    private Date fechaBusqueda;
    private Date horaBusqueda;

    /**
     * Constructor de la clase
     * @param link Host buscado
     * @param fechaBusqueda Fecha de la busqueda
     * @param horaBusqueda Hora de la busqueda
     */
    public Busqueda (String link, Date fechaBusqueda, Date horaBusqueda){
        this.link = link;
        this.fechaBusqueda = fechaBusqueda;
        this.horaBusqueda = horaBusqueda;
    }

    //Getter´s y Setter´s de los parametros
    public static int getId() {
        return Id;
    }

    public String getLink() {
        return link;
    }

    public Date getFechaBusqueda() { return fechaBusqueda; }

    public Date getHoraBusqueda() {
        return horaBusqueda;
    }

    public static void setId(int id) {
        Id = id;
    }

    public void setFechaBusqueda(Date fechaBusqueda) {
        this.fechaBusqueda = fechaBusqueda;
    }

    public void setHoraBusqueda(Date horaLlamada) {
        this.horaBusqueda = horaBusqueda;
    }

    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Metodo para mostrar la informacion de los Parametros
     * @return String con la información
     */
    public String toString() {
        return "Busqueda{" +
                "link='" + link + '\'' +
                ", fecha y Hora de la Busqueda=" + fechaBusqueda +
                '}';
    }
}
