package com.example.myapplication.utils;

import com.example.myapplication.data.models.Ejercicio;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Esta clase contiene todos los datos estáticos de los ejercicios predefinidos.
public class EjerciciosPredefinidos {

    // Un mapa estático que se crea una sola vez.
    // La CLAVE (String) es el tipo de rutina (ej. "Pecho").
    // El VALOR (List<Ejercicio>) es la lista de ejercicios para esa rutina.
    private static final Map<String, List<Ejercicio>> mapaEjercicios = new HashMap<>();

    // Bloque estático: se ejecuta 1 vez cuando la clase se carga en memoria.
    // Aquí definimos todas nuestras rutinas predefinidas.
    static {
        // --- RUTINA DE PECHO ---
        List<Ejercicio> ejerciciosPecho = new ArrayList<>();
        ejerciciosPecho.add(new Ejercicio("Press Banca", 4, 10));
        ejerciciosPecho.add(new Ejercicio("Press Inclinado", 3, 12));
        ejerciciosPecho.add(new Ejercicio("Aperturas con Mancuerna", 3, 15));
        mapaEjercicios.put("Pecho", ejerciciosPecho);

        // --- RUTINA DE PIERNA ---
        List<Ejercicio> ejerciciosPierna = new ArrayList<>();
        ejerciciosPierna.add(new Ejercicio("Sentadillas", 4, 10));
        ejerciciosPierna.add(new Ejercicio("Prensa de Piernas", 3, 12));
        ejerciciosPierna.add(new Ejercicio("Extensiones de Cuádriceps", 3, 15));
        ejerciciosPierna.add(new Ejercicio("Curl Femoral", 3, 15));
        mapaEjercicios.put("Pierna", ejerciciosPierna);

        // --- RUTINA DE ESPALDA ---
        List<Ejercicio> ejerciciosEspalda = new ArrayList<>();
        ejerciciosEspalda.add(new Ejercicio("Dominadas", 4, 8));
        ejerciciosEspalda.add(new Ejercicio("Remo con Barra", 4, 10));
        ejerciciosEspalda.add(new Ejercicio("Jalón al Pecho", 3, 12));
        mapaEjercicios.put("Espalda", ejerciciosEspalda);

        // --- AÑADE MÁS RUTINAS AQUÍ (ej. "Hombro", "Full Body", etc.) ---
    }

    // Metodo público que usan los ViewModels/Repositorios
    // para obtener la lista de ejercicios basada en un nombre.
    public static List<Ejercicio> getEjerciciosPorTipo(String tipoRutina) {
        // Devuelve la lista correspondiente.
        // Si no encuentra el "tipoRutina", devuelve una lista vacía para evitar crasheos.
        return mapaEjercicios.getOrDefault(tipoRutina, new ArrayList<>());
    }

    // Metodo público para obtener todos los nombres de rutinas disponibles
    // (Útil para un Spinner o Dropdown en la UI)
    public static String[] getTiposDeRutina() {
        return mapaEjercicios.keySet().toArray(new String[0]);
    }
}