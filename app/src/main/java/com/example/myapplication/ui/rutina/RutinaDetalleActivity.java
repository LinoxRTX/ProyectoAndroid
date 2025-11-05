package com.example.myapplication.ui.rutina;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.data.models.Ejercicio;
import com.example.myapplication.data.models.RutinaModel;

// Esta es la nueva Activity para mostrar los detalles
public class RutinaDetalleActivity extends AppCompatActivity {

    private TextView tvNombreRutina, tvDiasRutina, tvEjerciciosLista;
    private ImageButton btnVolverDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rutina_detalle);

        // Conectar Vistas
        tvNombreRutina = findViewById(R.id.tvNombreRutinaDetalle);
        tvDiasRutina = findViewById(R.id.tvDiasRutinaDetalle);
        tvEjerciciosLista = findViewById(R.id.tvEjerciciosListaDetalle);
        btnVolverDetalle = findViewById(R.id.btnVolverDetalle);

        // Configurar botón de volver
        btnVolverDetalle.setOnClickListener(v -> finish()); // "finish()" cierra la pantalla

        // Obtener la rutina que nos pasó la 'home.java'
        RutinaModel rutina = (RutinaModel) getIntent().getSerializableExtra("RUTINA_SELECCIONADA");

        if (rutina != null) {
            // Rellenar la UI con los datos de la rutina
            tvNombreRutina.setText(rutina.getNombreRutina());
            tvDiasRutina.setText(rutina.getDiasDeRutina());

            // Construir la lista de ejercicios
            StringBuilder sb = new StringBuilder();
            if (rutina.getEjercicios() != null) {
                for (Ejercicio ej : rutina.getEjercicios()) {
                    sb.append("• ") // Añade un bullet point
                            .append(ej.getNombre())
                            .append(" (")
                            .append(ej.getSeries())
                            .append("x")
                            .append(ej.getRepeticiones())
                            .append(")\n\n"); // Doble salto de línea
                }
            }
            tvEjerciciosLista.setText(sb.toString());

        } else {
            // Si algo sale mal y no recibe la rutina
            Toast.makeText(this, "Error al cargar la rutina", Toast.LENGTH_SHORT).show();
            finish();
        }

        // (Código de EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}