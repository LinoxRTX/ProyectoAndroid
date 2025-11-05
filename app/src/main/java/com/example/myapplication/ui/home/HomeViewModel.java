package com.example.myapplication.ui.home;

import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.data.AuthRepository;
import com.example.myapplication.data.FirebaseAuthRepository;
import com.example.myapplication.data.RutinaRepository;
import com.example.myapplication.data.RutinasCallback;
import com.example.myapplication.data.SimpleCallback; // <-- IMPORTADO
import com.example.myapplication.data.models.RutinaModel;
import com.example.myapplication.utils.GeocoderCallback;
import com.example.myapplication.utils.GeocoderHelper;
import com.example.myapplication.utils.Result;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    // --- Repositorios ---
    private FusedLocationProviderClient fusedLocationClient;
    private AuthRepository authRepository;
    private RutinaRepository rutinaRepository;

    // --- LiveData ---
    private MutableLiveData<String> _sessionStatus = new MutableLiveData<>();
    public LiveData<String> sessionStatus = _sessionStatus;
    private MutableLiveData<String> _locationStatus = new MutableLiveData<>();
    public LiveData<String> locationStatus = _locationStatus;
    private MutableLiveData<Boolean> _navigateToLogin = new MutableLiveData<>(false);
    public LiveData<Boolean> navigateToLogin = _navigateToLogin;
    private MutableLiveData<Result<List<RutinaModel>>> _rutinas = new MutableLiveData<>();
    public LiveData<Result<List<RutinaModel>>> rutinas = _rutinas;
    private MutableLiveData<String> _toastMessage = new MutableLiveData<>(); // <-- AÑADIDO
    public LiveData<String> toastMessage = _toastMessage; // <-- AÑADIDO

    public HomeViewModel(@NonNull Application application) {
        super(application);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application);
        authRepository = new FirebaseAuthRepository(application);
        rutinaRepository = new RutinaRepository();
    }

    public void initSession(boolean isGuestMode) {
        if (isGuestMode) {
            _sessionStatus.setValue("Modo: Invitado");
        } else {
            FirebaseUser user = authRepository.getCurrentUser();
            if (user != null) {
                _sessionStatus.setValue("Usuario: " + user.getEmail());
            } else {
                _sessionStatus.setValue("Modo: Invitado (Error)");
            }
        }
    }

    public void cargarRutinas(boolean isGuest) {
        _rutinas.setValue(new Result.Loading<>());
        rutinaRepository.obtenerRutinas(isGuest, new RutinasCallback() {
            @Override
            public void onRutinasCargadas(List<RutinaModel> listaRutinas) {
                _rutinas.setValue(new Result.Success<>(listaRutinas));
            }
            @Override
            public void onError(Exception e) {
                _rutinas.setValue(new Result.Error<>(e));
            }
        });
    }

    // --- MÉTODO ELIMINAR (CORREGIDO) ---
    // Ahora esta llamada coincide con el Repositorio (Archivo 1)
    public void eliminarRutina(RutinaModel rutina, boolean isGuest) {
        rutinaRepository.eliminarRutina(rutina, isGuest, new SimpleCallback() {
            @Override
            public void onSuccess() {
                // Recarga la lista DESPUÉS de que Firebase confirma la eliminación
                cargarRutinas(isGuest);
            }
            @Override
            public void onError(Exception e) {
                _toastMessage.postValue("Error al eliminar: " + e.getMessage());
            }
        });
    }
    // --- FIN DE LA CORRECCIÓN ---

    public void editarRutina(RutinaModel rutina) {
        Log.d("HomeViewModel", "Editar rutina: " + rutina.getNombreRutina());
    }

    public void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            _locationStatus.setValue("Error: Permiso no concedido.");
            return;
        }
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        getCityName(location.getLatitude(), location.getLongitude());
                    } else {
                        _locationStatus.setValue("No se pudo obtener ubicación.");
                    }
                })
                .addOnFailureListener(e -> _locationStatus.setValue("Error al obtener ubicación."));
    }

    private void getCityName(double latitude, double longitude) {
        GeocoderHelper.fetchCityName(getApplication(), latitude, longitude, new GeocoderCallback() {
            @Override
            public void onLocationNameFound(String locationName) {
                _locationStatus.postValue(locationName);
            }
            @Override
            public void onError(Exception e) {
                _locationStatus.postValue("Error de red de ubicación");
            }
        });
    }

    public void signOut() {
        authRepository.signOut(task -> {
            Log.d("HomeViewModel", "Usuario de Google deslogueado.");
            _navigateToLogin.postValue(true);
        });
    }
}