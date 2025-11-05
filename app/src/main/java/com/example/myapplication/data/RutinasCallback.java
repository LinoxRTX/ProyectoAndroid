package com.example.myapplication.data;

import com.example.myapplication.data.models.RutinaModel;
import java.util.List;

// Interfaz para devolver la lista de rutinas desde Firebase (que es asíncrono)
public interface RutinasCallback {
    // Se llama cuando se obtiene la lista de rutinas
    void onRutinasCargadas(List<RutinaModel> rutinas);

    // Se llama si hay un error
    void onError(Exception e);
}