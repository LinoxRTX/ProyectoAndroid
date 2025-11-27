package com.example.myapplication.data;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.myapplication.data.models.RutinaModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RutinaRepository {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRutinas;
    private DatabaseReference mDatabaseCounters;
    private DatabaseReference mDatabaseLogs;
    private static final String TAG = "RutinaRepository";

    private List<RutinaModel> rutinasEnMemoria = new ArrayList<>();

    public RutinaRepository() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        mDatabaseRutinas = db.getReference("rutinas");
        mDatabaseCounters = db.getReference("counters");
        mDatabaseLogs = db.getReference("logs");
    }

    public void guardarRutina(RutinaModel rutina, boolean isGuest, SimpleCallback callback) {
        if (isGuest) {
            rutina.setId("invitado_" + System.currentTimeMillis());
            rutinasEnMemoria.add(rutina);
            Log.d(TAG, "Rutina guardada en memoria (invitado).");
            callback.onSuccess();
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                DatabaseReference counterRef = mDatabaseCounters.child(uid).child("last_id");

                counterRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer currentId = mutableData.getValue(Integer.class);
                        int newId = (currentId == null) ? 1 : currentId + 1;
                        mutableData.setValue(newId);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                        if (committed && dataSnapshot != null) {
                            Integer newId = dataSnapshot.getValue(Integer.class);
                            if (newId != null) {
                                String formattedId = String.format(Locale.getDefault(), "%06d", newId);
                                rutina.setId(formattedId);

                                mDatabaseRutinas.child(uid).child(formattedId).setValue(rutina)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Rutina guardada en Firebase con ID: " + formattedId);
                                            registrarLog(uid, "CREAR", "Se creó la rutina: " + rutina.getNombreRutina());
                                            callback.onSuccess();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error al guardar rutina en Firebase", e);
                                            callback.onError(e);
                                        });
                            } else {
                                callback.onError(new Exception("Error al obtener nuevo ID"));
                            }
                        } else {
                            Log.e(TAG, "Error en la transacción", databaseError != null ? databaseError.toException() : null);
                            if(databaseError != null) callback.onError(databaseError.toException());
                            else callback.onError(new Exception("Error de transacción desconocido"));
                        }
                    }
                });
            } else {
                callback.onError(new Exception("Usuario no autenticado"));
            }
        }
    }

    public void obtenerRutinas(boolean isGuest, RutinasCallback callback) {
        if (isGuest) {
            callback.onRutinasCargadas(rutinasEnMemoria);
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                mDatabaseRutinas.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<RutinaModel> listaRutinas = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RutinaModel rutina = snapshot.getValue(RutinaModel.class);
                            if (rutina != null) {
                                listaRutinas.add(rutina);
                            }
                        }
                        callback.onRutinasCargadas(listaRutinas);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Error al leer rutinas de Firebase", databaseError.toException());
                        callback.onError(databaseError.toException());
                    }
                });
            } else {
                callback.onError(new Exception("Usuario no autenticado."));
            }
        }
    }

    public void actualizarRutina(RutinaModel rutina, boolean isGuest, SimpleCallback callback) {
        if (isGuest) {
            for (int i = 0; i < rutinasEnMemoria.size(); i++) {
                if (rutinasEnMemoria.get(i).getId().equals(rutina.getId())) {
                    rutinasEnMemoria.set(i, rutina);
                    Log.d(TAG, "Rutina actualizada en memoria.");
                    break;
                }
            }
            callback.onSuccess();
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null && rutina.getId() != null) {
                String uid = currentUser.getUid();
                mDatabaseRutinas.child(uid).child(rutina.getId()).setValue(rutina)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Rutina actualizada en Firebase.");
                            registrarLog(uid, "EDITAR", "Se actualizó la rutina ID: " + rutina.getId());
                            callback.onSuccess();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error al actualizar rutina", e);
                            callback.onError(e);
                        });
            } else {
                callback.onError(new Exception("Error al actualizar, usuario o ID nulo"));
            }
        }
    }

    public void eliminarRutina(RutinaModel rutina, boolean isGuest, SimpleCallback callback) {
        if (isGuest) {
            rutinasEnMemoria.removeIf(r -> r.getId().equals(rutina.getId()));
            Log.d(TAG, "Rutina eliminada de memoria.");
            callback.onSuccess();
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null && rutina.getId() != null) {
                String uid = currentUser.getUid();
                mDatabaseRutinas.child(uid).child(rutina.getId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Rutina eliminada de Firebase.");
                            registrarLog(uid, "ELIMINAR", "Se eliminó la rutina: " + rutina.getNombreRutina());
                            callback.onSuccess();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error al eliminar rutina", e);
                            callback.onError(e);
                        });
            } else {
                callback.onError(new Exception("Error al eliminar, usuario o ID nulo"));
            }
        }
    }

    private void registrarLog(String uid, String accion, String detalles) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("fecha", timestamp);
        logEntry.put("accion", accion);
        logEntry.put("detalles", detalles);
        mDatabaseLogs.child(uid).push().setValue(logEntry);
    }
}