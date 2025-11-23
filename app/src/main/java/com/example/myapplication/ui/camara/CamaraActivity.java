package com.example.myapplication.ui.camara;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.myapplication.R;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CamaraActivity extends AppCompatActivity {

    private static final String TAG = "CamaraActivity";
    private PreviewView viewFinder;
    private Button btnTomarFoto;
    private Button btnCambiarCamara;
    private Button btnFlash;
    private Button btnVolver;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private Camera camera;
    private int cameraSelector = CameraSelector.LENS_FACING_BACK;
    private int flashMode = ImageCapture.FLASH_MODE_OFF;
    private static final String PREFS_NAME = "CameraPrefs";
    private static final String KEY_START_DATE = "startDate";
    private SharedPreferences sharedPreferences;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camara);
        viewFinder = findViewById(R.id.viewFinder);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnCambiarCamara = findViewById(R.id.btnCambiarCamara);
        btnFlash = findViewById(R.id.btnFlash);
        btnVolver = findViewById(R.id.btnVolver);
        cameraExecutor = Executors.newSingleThreadExecutor();
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        updateFlashButtonText();
        checkAndSetStartDate();
        checkCameraPermission();
        btnTomarFoto.setOnClickListener(v -> takePhoto());
        btnCambiarCamara.setOnClickListener(v -> toggleCamera());
        btnFlash.setOnClickListener(v -> toggleFlash());
        btnVolver.setOnClickListener(v -> finish());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void checkAndSetStartDate() {
        if (!sharedPreferences.contains(KEY_START_DATE)) {
            sharedPreferences.edit().putLong(KEY_START_DATE, System.currentTimeMillis()).apply();
        }
    }
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder()
                        .setFlashMode(flashMode)
                        .build();
                CameraSelector selectedCameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraSelector)
                        .build();
                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(
                        this, selectedCameraSelector, preview, imageCapture);
                updateFlashButtonText();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error al iniciar la cámara", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;
        long startDateMillis = sharedPreferences.getLong(KEY_START_DATE, System.currentTimeMillis());
        long currentMillis = System.currentTimeMillis();
        long diffMillis = Math.abs(currentMillis - startDateMillis);
        long daysDiff = TimeUnit.MILLISECONDS.toDays(diffMillis) + 1;
        String fileName = "Día " + daysDiff + " de Cambio Físico";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Registro Fisico");
        }
        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues)
                        .build();
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        String msg = "Foto guardada: " + fileName;
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        String msg = "Error al guardar foto: " + exc.getMessage();
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, msg, exc);
                    }
                }
        );
    }

    private void toggleCamera() {
        if (cameraSelector == CameraSelector.LENS_FACING_BACK) {
            cameraSelector = CameraSelector.LENS_FACING_FRONT;
            flashMode = ImageCapture.FLASH_MODE_OFF;
        } else {
            cameraSelector = CameraSelector.LENS_FACING_BACK;
        }
        startCamera();
    }

    private void toggleFlash() {
        if (camera == null) return;
        if (camera.getCameraInfo().hasFlashUnit()) {
            if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                flashMode = ImageCapture.FLASH_MODE_ON;
            } else {
                flashMode = ImageCapture.FLASH_MODE_OFF;
            }
            imageCapture.setFlashMode(flashMode);
            updateFlashButtonText();
        } else {
            Toast.makeText(this, "La cámara frontal no tiene flash", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFlashButtonText() {
        if (flashMode == ImageCapture.FLASH_MODE_OFF) {
            btnFlash.setText("🔦: Apagada");
        } else {
            btnFlash.setText("🔦: Encendida");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}