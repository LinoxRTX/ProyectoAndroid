package com.example.myapplication.data;

import com.google.firebase.auth.FirebaseUser;
import java.lang.Exception;

// Esta interfaz es un "contrato" para manejar respuestas asíncronas.
// Permite al Repositorio devolver un resultado (éxito o error) al ViewModel
// una vez que Firebase haya respondido.
public interface AuthCallback {
    // Se llama cuando la operación de Firebase fue exitosa.
    // @param user El usuario autenticado.
    void onSuccess(FirebaseUser user);

    // Se llama cuando la operación de Firebase falló.
    // @param e La excepción o error que ocurrió.
    void onError(Exception e);
}