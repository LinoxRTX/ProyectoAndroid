package com.example.myapplication.data.models;

import java.io.Serializable;
import java.util.List;

public class RutinaModel implements Serializable {
    private String id;
    private String nombreRutina;
    private String diasDeRutina;
    private List<Ejercicio> ejercicios;
    public RutinaModel() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombreRutina() { return nombreRutina; }
    public void setNombreRutina(String nombreRutina) { this.nombreRutina = nombreRutina; }
    public String getDiasDeRutina() { return diasDeRutina; }
    public void setDiasDeRutina(String diasDeRutina) { this.diasDeRutina = diasDeRutina; }
    public List<Ejercicio> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<Ejercicio> ejercicios) { this.ejercicios = ejercicios; }
}