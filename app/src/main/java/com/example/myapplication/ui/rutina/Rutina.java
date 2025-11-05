package com.example.myapplication.ui.rutina;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair; // <-- IMPORTADO PARA RANGO
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.data.models.Ejercicio;
import com.google.android.material.datepicker.MaterialDatePicker; // <-- IMPORTADO
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener; // <-- IMPORTADO

import java.text.SimpleDateFormat; // <-- IMPORTADO
import java.util.Date; // <-- IMPORTADO
import java.util.List;
import java.util.Locale; // <-- IMPORTADO

public class Rutina extends AppCompatActivity {

    private RutinaViewModel viewModel;

    // Vistas de la UI
    private EditText etNombreRutina;
    private Spinner spinnerTipoRutina;
    private TextView tvEjerciciosLista;
    private Button btnGuardarRutina;
    private Button btnAbrirCalendario; // <-- NUEVO
    private TextView tvRangoFechas; // <-- NUEVO

    // Variable para guardar el string del rango (ej: "05/11/2025 - 05/12/2025")
    private String rangoSeleccionado = "";

    private boolean isGuestMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rutina);

        isGuestMode = getIntent().getBooleanExtra("IS_GUEST_MODE", true);

        // --- Conectar Vistas ---
        etNombreRutina = findViewById(R.id.etNombreRutina);
        spinnerTipoRutina = findViewById(R.id.spinnerTipoRutina);
        tvEjerciciosLista = findViewById(R.id.tvEjerciciosLista);
        btnGuardarRutina = findViewById(R.id.btnGuardarRutina);
        btnAbrirCalendario = findViewById(R.id.btnAbrirCalendario); // <-- NUEVO
        tvRangoFechas = findViewById(R.id.tvRangoFechas); // <-- NUEVO
        // ---

        viewModel = new ViewModelProvider(this).get(RutinaViewModel.class);

        // --- Configurar Listeners ---
        setupSpinner();
        setupCalendario(); // <-- NUEVO
        btnGuardarRutina.setOnClickListener(v -> guardarRutina());

        setupObservers();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // --- MÉTODO NUEVO PARA EL CALENDARIO ---
    private void setupCalendario() {
        // 1. Crear el constructor del MaterialDatePicker para un RANGO
        MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();

        builder.setTitleText("Selecciona el rango de la rutina");

        // 2. Construir el Picker
        final MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        // 3. Configurar el clic del botón para MOSTRAR el calendario
        btnAbrirCalendario.setOnClickListener(v -> {
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        // 4. Configurar qué hacer cuando el usuario presiona "OK"
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // "selection" contiene las fechas de inicio y fin en milisegundos (Long)
            Long fechaInicio = selection.first;
            Long fechaFin = selection.second;

            // Formateamos las fechas a un String legible
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaInicioStr = sdf.format(new Date(fechaInicio));
            String fechaFinStr = sdf.format(new Date(fechaFin));

            // Guardamos el rango y lo mostramos en el TextView
            rangoSeleccionado = fechaInicioStr + " - " + fechaFinStr;
            tvRangoFechas.setText(rangoSeleccionado);
        });
    }

    private void setupSpinner() {
        // ... (Este método no cambia)
        String[] tipos = viewModel.getTiposDeRutina();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoRutina.setAdapter(adapter);

        spinnerTipoRutina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipoSeleccionado = (String) parent.getItemAtPosition(position);
                viewModel.onTipoRutinaSeleccionado(tipoSeleccionado);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupObservers() {
        // ... (Este método no cambia)
        viewModel.ejercicios.observe(this, ejercicios -> {
            StringBuilder sb = new StringBuilder();
            for (Ejercicio ej : ejercicios) {
                sb.append(ej.getNombre())
                        .append(" (")
                        .append(ej.getSeries())
                        .append("x")
                        .append(ej.getRepeticiones())
                        .append(")\n");
            }
            tvEjerciciosLista.setText(sb.toString());
        });

        viewModel.cerrarActividad.observe(this, cerrar -> {
            if (cerrar) {
                Toast.makeText(this, "Rutina guardada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void guardarRutina() {
        String nombre = etNombreRutina.getText().toString();
        // Usamos el String del rango que guardamos
        String dias = rangoSeleccionado;

        // Validación
        if (nombre.isEmpty() || dias.isEmpty()) {
            Toast.makeText(this, "Por favor, completa el nombre y selecciona un rango de fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Le pasamos los datos al ViewModel
        viewModel.guardarRutina(nombre, dias, isGuestMode);
    }
}