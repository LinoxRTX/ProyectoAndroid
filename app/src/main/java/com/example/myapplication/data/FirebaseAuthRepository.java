package com.example.myapplication.data;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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

    @Override
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    @Override
    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }
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
    @Override
    public void signOut(OnCompleteListener<Void> listener) {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(listener);
    }
}