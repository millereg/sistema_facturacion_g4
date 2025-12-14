package com.cibertec.sistema_facturacion_g4.shared.exceptions;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
