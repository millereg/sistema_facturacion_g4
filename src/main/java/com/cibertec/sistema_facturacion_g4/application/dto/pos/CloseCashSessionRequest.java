package com.cibertec.sistema_facturacion_g4.application.dto.pos;

import lombok.Data;

@Data
public class CloseCashSessionRequest {
    private String finalAmount;
    private String notes;
}