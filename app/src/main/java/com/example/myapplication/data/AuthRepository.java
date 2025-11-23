package com.example.myapplication.data;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;

public interface AuthRepository {
    FirebaseUser getCurrentUser();
    GoogleSignInClient getGoogleSignInClient();
    void firebaseAuthWithGoogle(String idToken, AuthCallback callback);
    void signOut(OnCompleteListener<Void> listener);
}