package com.example.myapplication.data.models;

import java.io.Serializable; // <-- 1. IMPORTADO

// 2. AÑADIDO "implements Serializable"
public class Ejercicio implements Serializable {
    private String nombre;
    private int series;
    private int repeticiones;

    // Constructor vacío (OBLIGATORIO para Firebase)
    public Ejercicio() {}

    // Constructor que estamos usando en "EjerciciosPredefinidos"
    public Ejercicio(String nombre, int series, int repeticiones) {
        this.nombre = nombre;
        this.series = series;
        this.repeticiones = repeticiones;
    }

    // Getters y Setters (OBLIGATORIOS para Firebase)
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getSeries() { return series; }
    public void setSeries(int series) { this.series = series; }

    public int getRepeticiones() { return repeticiones; }
    public void setRepeticiones(int repeticiones) { this.repeticiones = repeticiones; }
}