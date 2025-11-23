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
import androidx.lifecycle.ViewModelProvider;
import com.example.myapplication.R;
import com.example.myapplication.ui.home.home;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class Login extends AppCompatActivity {

    private MaterialButton btnGoogleSignIn;
    private MaterialButton btnContinueAsGuest;
    private LoginViewModel viewModel;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnContinueAsGuest = findViewById(R.id.btnContinueAsGuest);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
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
        btnGoogleSignIn.setOnClickListener(v -> {
            viewModel.startGoogleSignIn();
        });
        btnContinueAsGuest.setOnClickListener(v -> {
            viewModel.onGuestClicked();
        });
        setupObservers();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void setupObservers() {
        viewModel.googleSignInIntent.observe(this, intent -> {
            if (intent != null) {
                googleSignInLauncher.launch(intent);
            }
        });
        viewModel.navigateToHomeAsUser.observe(this, navigate -> {
            if (navigate) {
                navigateToHome(false); // false = no es invitado
            }
        });
        viewModel.navigateToHomeAsGuest.observe(this, navigate -> {
            if (navigate) {
                navigateToHome(true); // true = es invitado
            }
        });
        viewModel.toastMessage.observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        viewModel.checkUserStatus();
    }
    private void navigateToHome(boolean isGuest) {
        Intent intent = new Intent(Login.this, home.class);
        intent.putExtra("IS_GUEST_MODE", isGuest);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}