package com.example.myapplication.utils;

import java.lang.Exception;

// Interfaz para devolver el resultado del Geocoder (que es asíncrono)
public interface GeocoderCallback {
    void onLocationNameFound(String locationName);
    void onError(Exception e);
}