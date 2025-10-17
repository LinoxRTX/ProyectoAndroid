package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class home extends AppCompatActivity {

    private boolean isGuestMode = true;

    private TextView tvSessionStatus;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private TextView tvLocationFooter;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        tvSessionStatus = findViewById(R.id.tvSessionStatus);
        tvLocationFooter = findViewById(R.id.tvLocationFooter);
        mAuth = FirebaseAuth.getInstance();

        String defaultWebClientId = "483302094425-03artet4h75kight70rsijc176til3j4.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(defaultWebClientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d("HomeActivity", "Permiso de ubicación concedido.");
                        getLocation();
                    } else {
                        Log.d("HomeActivity", "Permiso de ubicación denegado.");
                        tvLocationFooter.setText("Ubicación denegada.");
                    }
                }
        );

        checkAndRequestLocationPermission();

        isGuestMode = getIntent().getBooleanExtra("IS_GUEST_MODE", true);
        if (isGuestMode) {
            Log.d("HomeActivity", "Usuario en Modo Invitado.");
            tvSessionStatus.setText("Modo: Invitado");
        } else {
            Log.d("HomeActivity", "Usuario Logueado.");
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                tvSessionStatus.setText("Usuario: " + user.getEmail());
            } else {
                tvSessionStatus.setText("Modo: Invitado (Error)");
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkAndRequestLocationPermission() {
        String permission = android.Manifest.permission.ACCESS_FINE_LOCATION;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            tvLocationFooter.setText("Error: Permiso no concedido.");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Log.d("HomeActivity", "Ubicación obtenida: " + location.getLatitude() + ", " + location.getLongitude());
                        getCityName(location.getLatitude(), location.getLongitude());
                    } else {
                        Log.w("HomeActivity", "No se pudo obtener la última ubicación (es null).");
                        tvLocationFooter.setText("No se pudo obtener ubicación.");
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e("HomeActivity", "Error al obtener ubicación", e);
                    tvLocationFooter.setText("Error de ubicación.");
                });
    }

    private void getCityName(double latitude, double longitude) {
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String city = address.getLocality();
                    String country = address.getCountryName();

                    String locationText;
                    if (city != null) {
                        locationText = city + ", " + country;
                    } else {
                        locationText = country;
                    }

                    runOnUiThread(() -> {
                        if (locationText != null && !locationText.isEmpty()) {
                            tvLocationFooter.setText(locationText);
                        } else {
                            tvLocationFooter.setText("Ubicación desconocida");
                        }
                    });

                } else {
                    Log.w("HomeActivity", "Geocoder no encontró direcciones.");
                    runOnUiThread(() -> tvLocationFooter.setText("Ubicación desconocida"));
                }

            } catch (IOException e) {
                Log.e("HomeActivity", "Error de Geocoder", e);
                runOnUiThread(() -> tvLocationFooter.setText("Error de red de ubicación"));
            }
        }).start();
    }

    public void rutina (View view){
        Intent intent = new Intent(this, Rutina.class);
        intent.putExtra("IS_GUEST_MODE", isGuestMode);
        startActivity(intent);
    }

    public void imc (View view){
        Intent intent = new Intent(this, Imc.class);
        startActivity(intent);
    }

    public void cerrarSesion(View view) {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Log.d("HomeActivity", "Usuario de Google deslogueado.");
            Intent intent = new Intent(home.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}