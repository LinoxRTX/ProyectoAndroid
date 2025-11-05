package com.example.myapplication.utils;

// Una clase genérica que envuelve una respuesta de un Repositorio.
// Contiene el estado de los datos (Success, Error, or Loading).
// <T> es el tipo de dato que esperamos, ej: Result<String> o Result<RutinaModel>
public class Result<T> {

    // Constructor privado para que solo se pueda crear desde dentro
    private Result() {}

    // --- LOS TRES ESTADOS POSIBLES ---

    // Estado de Éxito. Contiene los datos.
    public static final class Success<T> extends Result<T> {
        public T data;

        public Success(T data) {
            this.data = data;
        }
    }

    // Estado de Error. Contiene el error.
    public static final class Error<T> extends Result<T> {
        public Exception exception;

        public Error(Exception exception) {
            this.exception = exception;
        }
    }

    // Estado de Carga. No contiene datos.
    public static final class Loading<T> extends Result<T> {
        public Loading() {}
    }
}