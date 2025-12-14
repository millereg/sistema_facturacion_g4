package com.cibertec.sistema_facturacion_g4.infrastructure.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "No se puede realizar la operación";
        String errorMsg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        
        if (errorMsg.contains("fk_detail_product") || errorMsg.contains("invoice_details")) {
            message = "No se puede eliminar el producto porque está asociado a facturas";
        } else if (errorMsg.contains("fk_product_category") || errorMsg.contains("product")) {
            message = "No se puede eliminar la categoría porque tiene productos asociados";
        } else if (errorMsg.contains("fk_product_supplier")) {
            message = "No se puede eliminar el proveedor porque tiene productos asociados";
        } else if (errorMsg.contains("invoice")) {
            message = "No se puede eliminar el cliente porque tiene facturas asociadas";
        } else if (errorMsg.contains("foreign key constraint")) {
            message = "No se puede eliminar porque existen registros relacionados";
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("error", "DataIntegrityViolation");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        System.err.println("ERROR INESPERADO: " + ex.getMessage());
        ex.printStackTrace();
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("error", ex.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
