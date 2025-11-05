package com.example.myapplication.data;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener; // <-- IMPORTANTE
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

// Esta clase le dice a Java: "Yo cumplo el contrato AuthRepository"
public class FirebaseAuthRepository implements AuthRepository {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Context mApplicationContext;

    public FirebaseAuthRepository(Context applicationContext) {
        mAuth = FirebaseAuth.getInstance();
        this.mApplicationContext = applicationContext;

        String defaultWebClientId = "483302094425-03artet4h75kight70rsijc176til3j4.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(defaultWebClientId)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(applicationContext, gso);
    }

    // --- Cumpliendo el Contrato 1 ---
    @Override
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // --- Cumpliendo el Contrato 2 ---
    @Override
    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    // --- Cumpliendo el Contrato 3 ---
    @Override
    public void firebaseAuthWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ContextCompat.getMainExecutor(mApplicationContext), task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }

    // --- CUMPLIENDO EL CONTRATO 4 (EL QUE FALTABA) ---
    @Override
    public void signOut(OnCompleteListener<Void> listener) {
        // 1. Cierra sesión en Firebase
        mAuth.signOut();
        // 2. Cierra sesión en Google y avisa al listener cuando termine
        mGoogleSignInClient.signOut().addOnCompleteListener(listener);
    }
}