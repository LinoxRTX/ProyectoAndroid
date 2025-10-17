package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.DecimalFormat;

public class Imc extends AppCompatActivity {

    EditText etPeso, etAltura;
    Button btnCalcularIMC;
    TextView tvResultadoIMC, tvClasificacionIMC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_imc);

        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        btnCalcularIMC = findViewById(R.id.btnCalcularIMC);
        tvResultadoIMC = findViewById(R.id.tvResultadoIMC);
        tvClasificacionIMC = findViewById(R.id.tvClasificacionIMC);

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

    private void calculateAndDisplayBmi() {
        String weightString = etPeso.getText().toString();
        String heightString = etAltura.getText().toString();

        if (weightString.isEmpty() || heightString.isEmpty()) {
            Toast.makeText(this, "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            float weightKg = Float.parseFloat(weightString);
            float heightCm = Float.parseFloat(heightString);

            if (heightCm <= 0) {
                Toast.makeText(this, "La altura debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }
            float heightMeters = heightCm / 100f;
            float bmi = weightKg / (heightMeters * heightMeters);
            String classification = getBmiClassification(bmi);
            DecimalFormat df = new DecimalFormat("#.##");
            String formattedBmi = df.format(bmi);
            tvResultadoIMC.setText("IMC: " + formattedBmi);
            tvClasificacionIMC.setText("Clasificación: " + classification);
        } catch (NumberFormatException e) {

            Toast.makeText(this, "Por favor, ingresa números válidos", Toast.LENGTH_SHORT).show();
        }
    }
    private String getBmiClassification(float bmi) {
        if (bmi < 18.5) {
            return "Bajo peso";
        } else if (bmi < 24.9) {
            return "Peso normal";
        } else if (bmi < 29.9) {
            return "Sobrepeso";
        } else if (bmi < 34.9) {
            return "Obesidad Grado I";
        } else if (bmi < 39.9) {
            return "Obesidad Grado II";
        } else {
            return "Obesidad Grado III (Mórbida)";
        }
    }
}