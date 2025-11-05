package com.example.myapplication.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider; // Importar ViewModel

import com.example.myapplication.R;
import com.example.myapplication.ui.home.home;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

// --- ¡YA NO SE IMPORTA NADA DE FIREBASE AUTH! ---
// Toda esa lógica se movió al Repositorio y al ViewModel.

// La "Vista" (View) del patrón MVVM.
// Es una clase "tonta" que solo sabe cómo:
// 1. Dibujar la UI (el XML).
// 2. Recibir clics del usuario y avisar al ViewModel.
// 3. Observar el ViewModel y reaccionar a los cambios.
public class Login extends AppCompatActivity {

    // --- Variables de la Vista (UI) ---
    private MaterialButton btnGoogleSignIn;
    private MaterialButton btnContinueAsGuest;

    // --- Variables del "Cerebro" (ViewModel) y Launcher ---
    private LoginViewModel viewModel;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // 1. Inicializar Vistas
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnContinueAsGuest = findViewById(R.id.btnContinueAsGuest);

        // 2. Conectar el ViewModel ("Cerebro")
        // ViewModelProvider se asegura de que el ViewModel sobreviva a giros de pantalla.
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 3. Configurar el Launcher de Google
        // El Launcher ahora solo recibe el resultado y se lo pasa al ViewModel.
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        // La Vista no sabe qué hacer con "task", solo se lo pasa al ViewModel.
                        viewModel.handleGoogleSignInResult(task);
                    } else {
                        Log.w(TAG, "Google sign in cancelled by user.");
                    }
                }
        );

        // 4. Configurar Click Listeners
        // La Vista solo le dice al ViewModel "el usuario hizo clic"
        btnGoogleSignIn.setOnClickListener(v -> {
            viewModel.startGoogleSignIn();
        });
        btnContinueAsGuest.setOnClickListener(v -> {
            viewModel.onGuestClicked();
        });

        // 5. Configurar Observadores
        // La Vista "escucha" las órdenes del ViewModel
        setupObservers();

        // 6. Configurar UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Configura los observadores de LiveData.
    // Aquí es donde la Vista se suscribe a los cambios del ViewModel.
    private void setupObservers() {

        // Observador para el Intent de Google
        // El ViewModel nos da el Intent, y la Vista (Activity) lo lanza.
        viewModel.googleSignInIntent.observe(this, intent -> {
            if (intent != null) {
                googleSignInLauncher.launch(intent);
            }
        });

        // Observador para navegar como Usuario
        viewModel.navigateToHomeAsUser.observe(this, navigate -> {
            if (navigate) {
                navigateToHome(false); // false = no es invitado
            }
        });

        // Observador para navegar como Invitado
        viewModel.navigateToHomeAsGuest.observe(this, navigate -> {
            if (navigate) {
                navigateToHome(true); // true = es invitado
            }
        });

        // Observador para mostrar Toasts
        // El ViewModel nos dice qué mensaje mostrar.
        viewModel.toastMessage.observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // La Vista solo le avisa al ViewModel que chequee el estado del usuario.
        viewModel.checkUserStatus();
    }

    // El método de navegación se queda en la Vista,
    // ya que la navegación (Intents) es una responsabilidad de la UI.
    private void navigateToHome(boolean isGuest) {
        Intent intent = new Intent(Login.this, home.class);
        intent.putExtra("IS_GUEST_MODE", isGuest);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}