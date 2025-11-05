package com.example.myapplication.ui.rutina;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.data.RutinaRepository;
import com.example.myapplication.data.SimpleCallback; // <-- IMPORTADO
import com.example.myapplication.data.models.Ejercicio;
import com.example.myapplication.data.models.RutinaModel;
import com.example.myapplication.utils.EjerciciosPredefinidos;

import java.util.List;

public class RutinaViewModel extends AndroidViewModel {

    private RutinaRepository rutinaRepository;

    private MutableLiveData<List<Ejercicio>> _ejercicios = new MutableLiveData<>();
    public LiveData<List<Ejercicio>> ejercicios = _ejercicios;

    private MutableLiveData<Boolean> _cerrarActividad = new MutableLiveData<>(false);
    public LiveData<Boolean> cerrarActividad = _cerrarActividad;

    private MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public LiveData<String> toastMessage = _toastMessage;

    public RutinaViewModel(Application application) {
        super(application);
        rutinaRepository = new RutinaRepository();
    }

    public String[] getTiposDeRutina() {
        return EjerciciosPredefinidos.getTiposDeRutina();
    }

    public void onTipoRutinaSeleccionado(String tipoRutina) {
        List<Ejercicio> lista = EjerciciosPredefinidos.getEjerciciosPorTipo(tipoRutina);
        _ejercicios.setValue(lista);
    }

    // --- MÉTODO GUARDAR (CORREGIDO CON CALLBACK) ---
    public void guardarRutina(String nombreRutina, String diasRutina, boolean isGuest) {
        List<Ejercicio> ejerciciosActuales = _ejercicios.getValue();

        RutinaModel nuevaRutina = new RutinaModel();
        nuevaRutina.setNombreRutina(nombreRutina);
        nuevaRutina.setDiasDeRutina(diasRutina);
        nuevaRutina.setEjercicios(ejerciciosActuales);

        // ¡CORRECCIÓN! Añadimos el callback que faltaba
        rutinaRepository.guardarRutina(nuevaRutina, isGuest, new SimpleCallback() {
            @Override
            public void onSuccess() {
                // Solo cerramos la actividad DESPUÉS de que se guardó
                _cerrarActividad.postValue(true);
            }
            @Override
            public void onError(Exception e) {
                Log.e("RutinaViewModel", "Error al guardar", e);
                _toastMessage.postValue("Error al guardar: " + e.getMessage());
            }
        });

        // La línea _cerrarActividad.setValue(true) se borra de aquí
    }
    // --- FIN DE LA CORRECCIÓN ---

    // --- MÉTODO ACTUALIZAR (ACTUALIZADO CON CALLBACK) ---
    public void actualizarRutina(RutinaModel rutinaExistente, String nuevoNombre, String nuevosDias, boolean isGuest) {
        List<Ejercicio> ejerciciosActuales = _ejercicios.getValue();

        rutinaExistente.setNombreRutina(nuevoNombre);
        rutinaExistente.setDiasDeRutina(nuevosDias);
        rutinaExistente.setEjercicios(ejerciciosActuales);

        rutinaRepository.actualizarRutina(rutinaExistente, isGuest, new SimpleCallback() {
            @Override
            public void onSuccess() {
                _cerrarActividad.postValue(true);
            }
            @Override
            public void onError(Exception e) {
                _toastMessage.postValue("Error al actualizar: " + e.getMessage());
            }
        });
    }
}