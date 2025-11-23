package com.example.myapplication.ui.rutina;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.data.models.Ejercicio;
import com.example.myapplication.data.models.RutinaModel;
import com.example.myapplication.utils.EjerciciosPredefinidos;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Rutina extends AppCompatActivity {

    private RutinaViewModel viewModel;
    private EditText etNombreRutina;
    private Spinner spinnerTipoRutina;
    private TextView tvEjerciciosLista;
    private Button btnGuardarRutina;
    private Button btnAbrirCalendario;
    private TextView tvRangoFechas;
    private ImageButton btnVolver;
    private String rangoSeleccionado = "";
    private boolean isGuestMode = true;
    private boolean isEditMode = false;
    private RutinaModel rutinaParaEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rutina);
        isGuestMode = getIntent().getBooleanExtra("IS_GUEST_MODE", true);
        etNombreRutina = findViewById(R.id.etNombreRutina);
        spinnerTipoRutina = findViewById(R.id.spinnerTipoRutina);
        tvEjerciciosLista = findViewById(R.id.tvEjerciciosLista);
        btnGuardarRutina = findViewById(R.id.btnGuardarRutina);
        btnAbrirCalendario = findViewById(R.id.btnAbrirCalendario);
        tvRangoFechas = findViewById(R.id.tvRangoFechas);
        btnVolver = findViewById(R.id.btnVolver);
        viewModel = new ViewModelProvider(this).get(RutinaViewModel.class);
        setupSpinner();
        setupCalendario();
        btnGuardarRutina.setOnClickListener(v -> guardarOActualizarRutina());
        btnVolver.setOnClickListener(v -> finish());
        setupObservers();
        if (getIntent().hasExtra("RUTINA_PARA_EDITAR")) {
            isEditMode = true;
            rutinaParaEditar = (RutinaModel) getIntent().getSerializableExtra("RUTINA_PARA_EDITAR");
            prepopularCampos();
        } else {
            isEditMode = false;
            viewModel.onTipoRutinaSeleccionado(viewModel.getTiposDeRutina()[0]);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void prepopularCampos() {
        if (rutinaParaEditar == null) return;
        btnGuardarRutina.setText("Actualizar Rutina");
        etNombreRutina.setText(rutinaParaEditar.getNombreRutina());
        rangoSeleccionado = rutinaParaEditar.getDiasDeRutina();
        tvRangoFechas.setText(rangoSeleccionado);
        String tipoDetectado = "";
        if (rutinaParaEditar.getEjercicios() != null) {
            tipoDetectado = EjerciciosPredefinidos.detectarTipoDeRutina(rutinaParaEditar.getEjercicios());
        }
        String[] tipos = viewModel.getTiposDeRutina();
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].equals(tipoDetectado)) {
                spinnerTipoRutina.setSelection(i);
                break;
            }
        }
    }

    private void setupCalendario() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Selecciona el rango");
        final MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        btnAbrirCalendario.setOnClickListener(v -> {
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Long fechaInicio = selection.first;
            Long fechaFin = selection.second;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            rangoSeleccionado = sdf.format(new Date(fechaInicio)) + " - " + sdf.format(new Date(fechaFin));
            tvRangoFechas.setText(rangoSeleccionado);
        });
    }

    private void setupSpinner() {
        String[] tipos = viewModel.getTiposDeRutina();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
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
        viewModel.ejercicios.observe(this, ejercicios -> {
            StringBuilder sb = new StringBuilder();
            for (Ejercicio ej : ejercicios) {
                sb.append("• ").append(ej.getNombre())
                        .append(" (").append(ej.getSeries()).append("x").append(ej.getRepeticiones()).append(")\n\n");
            }
            tvEjerciciosLista.setText(sb.toString());
        });
        viewModel.cerrarActividad.observe(this, cerrar -> {
            if (cerrar) {
                String msg = isEditMode ? "Rutina actualizada correctamente" : "Rutina creada correctamente";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        viewModel.toastMessage.observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void guardarOActualizarRutina() {
        String nombre = etNombreRutina.getText().toString();
        String dias = rangoSeleccionado;
        if (nombre.isEmpty() || dias.isEmpty()) {
            Toast.makeText(this, "Completa el nombre y la fecha", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEditMode) {
            viewModel.actualizarRutina(rutinaParaEditar, nombre, dias, isGuestMode);
        } else {
            viewModel.guardarRutina(nombre, dias, isGuestMode);
        }
    }
}