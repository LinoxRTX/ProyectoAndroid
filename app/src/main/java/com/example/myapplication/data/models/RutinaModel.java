package com.example.myapplication.data.models;

import java.io.Serializable; // <-- 1. IMPORTADO
import java.util.List;

// Este es el objeto POJO (Plain Old Java Object) que se guardará en Firebase.
// 2. AÑADIDO "implements Serializable"
public class RutinaModel implements Serializable {

    // (El ID único lo generará Firebase, pero lo guardamos aquí para referencia)
    private String id;
    private String nombreRutina;
    private String diasDeRutina;
    private List<Ejercicio> ejercicios; // La lista de ejercicios predefinidos

    // Constructor vacío (OBLIGATORIO para que Firebase pueda leer los datos)
    public RutinaModel() {}

    // --- Getters y Setters (OBLIGATORIOS para Firebase) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombreRutina() { return nombreRutina; }
    public void setNombreRutina(String nombreRutina) { this.nombreRutina = nombreRutina; }

    public String getDiasDeRutina() { return diasDeRutina; }
    public void setDiasDeRutina(String diasDeRutina) { this.diasDeRutina = diasDeRutina; }

    public List<Ejercicio> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<Ejercicio> ejercicios) { this.ejercicios = ejercicios; }
}