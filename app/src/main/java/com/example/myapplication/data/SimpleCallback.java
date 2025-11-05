package com.example.myapplication.data;

// Interfaz genérica para operaciones que no devuelven datos (guardar, borrar)
public interface SimpleCallback {
    void onSuccess();
    void onError(Exception e);
}