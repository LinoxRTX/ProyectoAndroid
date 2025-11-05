package com.example.myapplication.ui.home.adapter;

import com.example.myapplication.data.models.RutinaModel;

// Interfaz para manejar los clics en los botones (Editar/Eliminar) y en la tarjeta (Ver detalles)
public interface OnRutinaClickListener {
    void onEditarClick(RutinaModel rutina);
    void onEliminarClick(RutinaModel rutina);
    void onRutinaClick(RutinaModel rutina); // <-- LÍNEA ACTUALIZADA (descomentada)
}