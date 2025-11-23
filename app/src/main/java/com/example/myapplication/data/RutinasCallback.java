package com.example.myapplication.data;

import com.example.myapplication.data.models.RutinaModel;
import java.util.List;

public interface RutinasCallback {
    void onRutinasCargadas(List<RutinaModel> rutinas);
    void onError(Exception e);
}