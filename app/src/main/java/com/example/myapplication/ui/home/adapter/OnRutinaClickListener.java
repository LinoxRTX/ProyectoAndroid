package com.example.myapplication.ui.home.adapter;

import com.example.myapplication.data.models.RutinaModel;

public interface OnRutinaClickListener {
    void onEditarClick(RutinaModel rutina);
    void onEliminarClick(RutinaModel rutina);
    void onRutinaClick(RutinaModel rutina);
}