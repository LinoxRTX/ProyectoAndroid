package com.example.myapplication.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.RutinaModel;
import com.example.myapplication.ui.home.adapter.OnRutinaClickListener;
import com.example.myapplication.ui.home.adapter.RutinasAdapter;
import com.example.myapplication.ui.imc.Imc;
import com.example.myapplication.ui.login.Login;
import com.example.myapplication.ui.rutina.Rutina;
import com.example.myapplication.ui.rutina.RutinaDetalleActivity;
import com.example.myapplication.utils.Result;

import java.util.List;

// Implementa la interfaz (que ahora tiene 3 métodos)
public class home extends AppCompatActivity implements OnRutinaClickListener {

    private TextView tvSessionStatus;
    private TextView tvLocationFooter;
    private HomeViewModel viewModel;
    private boolean isGuestMode = true;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private RecyclerView rvRutinas;
    private RutinasAdapter rutinasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        tvSessionStatus = findViewById(R.id.tvSessionStatus);
        tvLocationFooter = findViewById(R.id.tvLocationFooter);
        rvRutinas = findViewById(R.id.rvRutinas);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        isGuestMode = getIntent().getBooleanExtra("IS_GUEST_MODE", true);

        viewModel.initSession(isGuestMode);

        setupRecyclerView();
        setupObservers();
        setupPermissionLauncher();
        checkAndRequestLocationPermission();

        // viewModel.cargarRutinas(isGuestMode); // <-- ESTA LÍNEA SE MOVIÓ A onResume()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // --- MÉTODO AÑADIDO ---
    @Override
    protected void onResume() {
        super.onResume();
        // Cada vez que la pantalla 'home' se ponga visible (incluyendo
        // cuando regresas de crear una rutina), se llamará a este método.
        Log.d("HomeActivity", "onResume: Recargando rutinas...");
        viewModel.cargarRutinas(isGuestMode); // <-- AHORA SE CARGA AQUÍ
    }
    // ---

    private void setupRecyclerView() {
        rutinasAdapter = new RutinasAdapter(this);
        rvRutinas.setLayoutManager(new LinearLayoutManager(this));
        rvRutinas.setAdapter(rutinasAdapter);
    }

    private void setupObservers() {
        viewModel.sessionStatus.observe(this, status -> {
            tvSessionStatus.setText(status);
        });

        viewModel.locationStatus.observe(this, location -> {
            tvLocationFooter.setText(location);
        });

        viewModel.navigateToLogin.observe(this, navigate -> {
            if (navigate) {
                Intent intent = new Intent(home.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        viewModel.rutinas.observe(this, resultado -> {
            if (resultado instanceof Result.Loading) {
                Log.d("HomeActivity", "Cargando rutinas...");
            } else if (resultado instanceof Result.Success) {
                List<RutinaModel> lista = ((Result.Success<List<RutinaModel>>) resultado).data;
                rutinasAdapter.setRutinas(lista);
                Log.d("HomeActivity", "Rutinas cargadas: " + lista.size());
            } else if (resultado instanceof Result.Error) {
                Exception e = ((Result.Error<List<RutinaModel>>) resultado).exception;
                Log.e("HomeActivity", "Error al cargar rutinas", e);
                Toast.makeText(this, "Error al cargar rutinas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d("HomeActivity", "Permiso de ubicación concedido.");
                        viewModel.fetchLocation();
                    } else {
                        Log.d("HomeActivity", "Permiso de ubicación denegado.");
                        tvLocationFooter.setText("Ubicación denegada.");
                    }
                }
        );
    }

    private void checkAndRequestLocationPermission() {
        String permission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchLocation();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    // --- Métodos de Navegación (sin cambios) ---
    public void rutina(View view) {
        Intent intent = new Intent(this, Rutina.class);
        intent.putExtra("IS_GUEST_MODE", isGuestMode);
        startActivity(intent);
    }

    public void imc(View view) {
        Intent intent = new Intent(this, Imc.class);
        startActivity(intent);
    }

    public void cerrarSesion(View view) {
        viewModel.signOut();
    }

    // --- MÉTODOS DE LA INTERFAZ OnRutinaClickListener ---

    @Override
    public void onEditarClick(RutinaModel rutina) {
        viewModel.editarRutina(rutina);
        Toast.makeText(this, "Editando: " + rutina.getNombreRutina(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEliminarClick(RutinaModel rutina) {
        viewModel.eliminarRutina(rutina, isGuestMode);
        Toast.makeText(this, "Eliminada: " + rutina.getNombreRutina(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRutinaClick(RutinaModel rutina) {
        // Creamos el Intent para abrir la nueva pantalla de detalle
        Intent intent = new Intent(this, RutinaDetalleActivity.class);

        // Adjuntamos la rutina completa (que hicimos Serializable)
        intent.putExtra("RUTINA_SELECCIONADA", rutina);

        // Lanzamos la nueva Activity
        startActivity(intent);
    }
}