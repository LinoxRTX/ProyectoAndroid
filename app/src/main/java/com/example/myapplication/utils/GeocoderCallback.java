package com.example.myapplication.utils;

import java.lang.Exception;

public interface GeocoderCallback {
    void onLocationNameFound(String locationName);
    void onError(Exception e);
}