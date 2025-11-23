package com.example.myapplication.data;

import com.google.firebase.auth.FirebaseUser;
import java.lang.Exception;

public interface AuthCallback {
    void onSuccess(FirebaseUser user);
    void onError(Exception e);
}