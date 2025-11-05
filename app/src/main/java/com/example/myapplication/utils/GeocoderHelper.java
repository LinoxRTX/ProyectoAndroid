package com.example.myapplication.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

// Clase de ayuda para manejar la lógica del Geocoder
public class GeocoderHelper {

    private static final String TAG = "GeocoderHelper";

    // Convertimos las coordenadas a un nombre de ciudad en un hilo separado
    public static void fetchCityName(Context context, double latitude, double longitude, GeocoderCallback callback) {

        // El Geocoder SIEMPRE debe correr en un hilo de fondo
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String city = address.getLocality();
                    String country = address.getCountryName();

                    String locationText = (city != null) ? city + ", " + country : country;

                    if (locationText != null && !locationText.isEmpty()) {
                        callback.onLocationNameFound(locationText);
                    } else {
                        Log.w(TAG, "Geocoder no encontró nombre de ubicación.");
                        callback.onError(new Exception("Ubicación desconocida"));
                    }
                } else {
                    Log.w(TAG, "Geocoder no encontró direcciones.");
                    callback.onError(new Exception("Ubicación desconocida"));
                }
            } catch (IOException e) {
                // Esto suele pasar si no hay conexión a internet
                Log.e(TAG, "Error de Geocoder (red?)", e);
                callback.onError(e);
            }
        }).start();
    }
}