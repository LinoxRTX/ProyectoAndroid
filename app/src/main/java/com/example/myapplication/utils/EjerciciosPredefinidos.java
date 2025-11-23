package com.example.myapplication.utils;

import com.example.myapplication.data.models.Ejercicio;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EjerciciosPredefinidos {

    private static final Map<String, List<Ejercicio>> mapaEjercicios = new HashMap<>();

    static {
        List<Ejercicio> ejerciciosPecho = new ArrayList<>();
        ejerciciosPecho.add(new Ejercicio("Press Banca", 4, 10));
        ejerciciosPecho.add(new Ejercicio("Press Inclinado con Mancuernas", 3, 12));
        ejerciciosPecho.add(new Ejercicio("Aperturas (Flyes)", 3, 15));
        ejerciciosPecho.add(new Ejercicio("Fondos en Paralelas", 3, 10));
        mapaEjercicios.put("Pecho", ejerciciosPecho);

        List<Ejercicio> ejerciciosPierna = new ArrayList<>();
        ejerciciosPierna.add(new Ejercicio("Sentadillas con Barra", 4, 10));
        ejerciciosPierna.add(new Ejercicio("Prensa de Piernas", 4, 12));
        ejerciciosPierna.add(new Ejercicio("Peso Muerto Rumano", 3, 10));
        ejerciciosPierna.add(new Ejercicio("Zancadas (Lunges)", 3, 12));
        ejerciciosPierna.add(new Ejercicio("Elevación de Talones (Gemelos)", 4, 20));
        mapaEjercicios.put("Pierna", ejerciciosPierna);

        List<Ejercicio> ejerciciosEspalda = new ArrayList<>();
        ejerciciosEspalda.add(new Ejercicio("Dominadas", 4, 8));
        ejerciciosEspalda.add(new Ejercicio("Remo con Barra", 4, 10));
        ejerciciosEspalda.add(new Ejercicio("Jalón al Pecho", 3, 12));
        ejerciciosEspalda.add(new Ejercicio("Remo Gironda", 3, 12));
        ejerciciosEspalda.add(new Ejercicio("Pull Over en Polea", 3, 15));
        mapaEjercicios.put("Espalda", ejerciciosEspalda);

        List<Ejercicio> ejerciciosHombro = new ArrayList<>();
        ejerciciosHombro.add(new Ejercicio("Press Militar", 4, 10));
        ejerciciosHombro.add(new Ejercicio("Elevaciones Laterales", 3, 15));
        ejerciciosHombro.add(new Ejercicio("Elevaciones Frontales", 3, 12));
        ejerciciosHombro.add(new Ejercicio("Pájaros (Posterior)", 3, 15));
        mapaEjercicios.put("Hombro", ejerciciosHombro);

        List<Ejercicio> ejerciciosBrazos = new ArrayList<>();
        ejerciciosBrazos.add(new Ejercicio("Curl de Bíceps con Barra", 3, 10));
        ejerciciosBrazos.add(new Ejercicio("Press Francés", 3, 10));
        ejerciciosBrazos.add(new Ejercicio("Curl Martillo", 3, 12));
        ejerciciosBrazos.add(new Ejercicio("Extensión de Tríceps en Polea", 3, 12));
        mapaEjercicios.put("Brazos", ejerciciosBrazos);

        List<Ejercicio> ejerciciosCardio = new ArrayList<>();
        ejerciciosCardio.add(new Ejercicio("Correr en Cinta (mins)", 1, 30));
        ejerciciosCardio.add(new Ejercicio("Plancha Abdominal (segs)", 3, 60));
        ejerciciosCardio.add(new Ejercicio("Crunch Abdominal", 4, 20));
        ejerciciosCardio.add(new Ejercicio("Burpees", 3, 15));
        mapaEjercicios.put("Cardio y Abdominales", ejerciciosCardio);

        List<Ejercicio> ejerciciosFullBody = new ArrayList<>();
        ejerciciosFullBody.add(new Ejercicio("Sentadillas", 3, 12));
        ejerciciosFullBody.add(new Ejercicio("Flexiones (Push-ups)", 3, 15));
        ejerciciosFullBody.add(new Ejercicio("Remo con Mancuerna", 3, 12));
        ejerciciosFullBody.add(new Ejercicio("Press Militar con Mancuernas", 3, 12));
        ejerciciosFullBody.add(new Ejercicio("Plancha (segs)", 3, 45));
        mapaEjercicios.put("Full Body", ejerciciosFullBody);

        List<Ejercicio> ejerciciosPosPucio = new ArrayList<>();
        ejerciciosPosPucio.add(new Ejercicio("llave Nudas", 6, 9));
        ejerciciosPosPucio.add(new Ejercicio("Reventones de centos", 3, 4));
        ejerciciosPosPucio.add(new Ejercicio("Chupones De Manguera", 13, 13));
        ejerciciosPosPucio.add(new Ejercicio("Agarradas de Manguaco", 6, 9));
        ejerciciosPosPucio.add(new Ejercicio("Say Gex", 3, 45));
        mapaEjercicios.put("Pos-Pucio", ejerciciosPosPucio);
    }

    public static List<Ejercicio> getEjerciciosPorTipo(String tipoRutina) {
        return mapaEjercicios.getOrDefault(tipoRutina, new ArrayList<>());
    }

    public static String[] getTiposDeRutina() {
        return mapaEjercicios.keySet().toArray(new String[0]);
    }

    public static String detectarTipoDeRutina(List<Ejercicio> ejerciciosDeLaRutina) {
        if (ejerciciosDeLaRutina == null || ejerciciosDeLaRutina.isEmpty()) {
            return "";
        }
        String nombreMuestra = ejerciciosDeLaRutina.get(0).getNombre();
        for (Map.Entry<String, List<Ejercicio>> entrada : mapaEjercicios.entrySet()) {
            String tipoRutina = entrada.getKey();
            List<Ejercicio> listaPredefinida = entrada.getValue();
            for (Ejercicio ejPredefinido : listaPredefinida) {
                if (ejPredefinido.getNombre().equals(nombreMuestra)) {
                    return tipoRutina;
                }
            }
        }
        return "";
    }
}