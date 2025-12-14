package com.cibertec.sistema_facturacion_g4.shared.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
