package com.example.myapplication.ui.login;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myapplication.data.AuthCallback;
import com.example.myapplication.data.AuthRepository;
import com.example.myapplication.data.FirebaseAuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {

    private AuthRepository authRepository;
    private static final String TAG = "LoginViewModel";
    private MutableLiveData<Boolean> _navigateToHomeAsUser = new MutableLiveData<>(false);
    public LiveData<Boolean> navigateToHomeAsUser = _navigateToHomeAsUser;
    private MutableLiveData<Boolean> _navigateToHomeAsGuest = new MutableLiveData<>(false);
    public LiveData<Boolean> navigateToHomeAsGuest = _navigateToHomeAsGuest;
    private MutableLiveData<Intent> _googleSignInIntent = new MutableLiveData<>();
    public LiveData<Intent> googleSignInIntent = _googleSignInIntent;
    private MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public LiveData<String> toastMessage = _toastMessage;
    public LoginViewModel(Application application) {
        super(application);
        authRepository = new FirebaseAuthRepository(application);
    }
    public void checkUserStatus() {
        if (authRepository.getCurrentUser() != null) {
            Log.d(TAG, "Usuario ya logueado.");
            _navigateToHomeAsUser.setValue(true);
        }
    }
    public void startGoogleSignIn() {
        Intent signInIntent = authRepository.getGoogleSignInClient().getSignInIntent();
        _googleSignInIntent.setValue(signInIntent);
    }
    public void onGuestClicked() {
        Log.d(TAG, "Continuando como invitado.");
        _navigateToHomeAsGuest.setValue(true);
    }
    public void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "Obtenido ID token de Google:" + account.getId());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            _toastMessage.setValue("Falló el inicio de sesión con Google");
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        authRepository.firebaseAuthWithGoogle(idToken, new AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d(TAG, "Logueo en Firebase exitoso: " + user.getDisplayName());
                _toastMessage.setValue("Bienvenido, " + user.getDisplayName());
                _navigateToHomeAsUser.setValue(true);
            }
            @Override
            public void onError(Exception e) {
                Log.w(TAG, "Fallo en signInWithCredential", e);
                _toastMessage.setValue("Error de autenticación.");
            }
        });
    }
}