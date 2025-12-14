package com.cibertec.sistema_facturacion_g4.application.dto.pos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {
    private boolean success;
    private String message;
    private String invoiceNumber;
    private BigDecimal totalAmount;
}