package com.example.myapplication.data;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener; // <-- 1. AÑADE ESTA IMPORTACIÓN
import com.google.firebase.auth.FirebaseUser;

// Este es el "Contrato"
public interface AuthRepository {

    // Contrato 1: Debe tener un método que devuelva un FirebaseUser
    FirebaseUser getCurrentUser();

    // Contrato 2: Debe tener un método que devuelva un GoogleSignInClient
    GoogleSignInClient getGoogleSignInClient();

    // Contrato 3: Debe tener un método para el login
    void firebaseAuthWithGoogle(String idToken, AuthCallback callback);

    // --- 2. AÑADE ESTE MÉTODO (EL QUE FALTABA) ---
    void signOut(OnCompleteListener<Void> listener);
}