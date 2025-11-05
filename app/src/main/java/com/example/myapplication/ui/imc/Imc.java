package com.example.myapplication.ui.imc;

import android.graphics.Color; // <-- IMPORTADO PARA LOS COLORES
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView; // Importado para la tarjeta
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;

public class Imc extends AppCompatActivity {

    TextInputEditText etPeso, etAltura;
    MaterialButton btnCalcularIMC;
    TextView tvResultadoIMC, tvClasificacionIMC;
    CardView cardResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_imc);

        // Conectar las vistas (IDs coinciden con el nuevo XML)
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        btnCalcularIMC = findViewById(R.id.btnCalcularIMC);
        tvResultadoIMC = findViewById(R.id.tvResultadoIMC);
        tvClasificacionIMC = findViewById(R.id.tvClasificacionIMC);
        cardResultado = findViewById(R.id.cardResultado);

        btnCalcularIMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAndDisplayBmi();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void volver(View view) {
        finish();
    }

    // --- MÉTODO ACTUALIZADO CON LÓGICA DE COLOR ---
    private void calculateAndDisplayBmi() {
        String weightString = etPeso.getText().toString();
        String heightString = etAltura.getText().toString();

        if (weightString.isEmpty() || heightString.isEmpty()) {
            Toast.makeText(this, "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show();
            cardResultado.setVisibility(View.GONE);
            return;
        }
        try {
            float weightKg = Float.parseFloat(weightString);
            float heightCm = Float.parseFloat(heightString);

            if (heightCm <= 0) {
                Toast.makeText(this, "La altura debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                cardResultado.setVisibility(View.GONE);
                return;
            }
            float heightMeters = heightCm / 100f;
            float bmi = weightKg / (heightMeters * heightMeters);

            // --- INICIO DE LA LÓGICA DE COLOR ---
            String classification;
            int classificationColor;

            if (bmi < 18.5) {
                classification = "Bajo peso";
                classificationColor = Color.parseColor("#9E9E9E"); // Grisáceo
            } else if (bmi < 24.9) {
                classification = "Peso normal";
                classificationColor = Color.parseColor("#4CAF50"); // Verde
            } else if (bmi < 29.9) {
                classification = "Sobrepeso";
                classificationColor = Color.parseColor("#FFC107"); // Amarillo (Ámbar)
            } else if (bmi < 34.9) {
                classification = "Obesidad Grado I";
                classificationColor = Color.parseColor("#FF9800"); // Naranjo
            } else if (bmi < 39.9) {
                classification = "Obesidad Grado II";
                classificationColor = Color.parseColor("#F44336"); // Rojo
            } else {
                classification = "Obesidad Grado III (Mórbida)";
                classificationColor = Color.parseColor("#000000"); // Negro
            }
            // --- FIN DE LA LÓGICA DE COLOR ---

            DecimalFormat df = new DecimalFormat("#.##");
            String formattedBmi = df.format(bmi);

            // Actualizar los textos y el color de la tarjeta
            tvResultadoIMC.setText(formattedBmi);
            tvClasificacionIMC.setText(classification);
            tvClasificacionIMC.setTextColor(classificationColor); // <-- ¡COLOR APLICADO!

            // Mostrar la tarjeta
            cardResultado.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, ingresa números válidos", Toast.LENGTH_SHORT).show();
            cardResultado.setVisibility(View.GONE);
        }
    }

    // El método getBmiClassification() ya no es necesario,
    // porque su lógica ahora vive dentro de calculateAndDisplayBmi()
}