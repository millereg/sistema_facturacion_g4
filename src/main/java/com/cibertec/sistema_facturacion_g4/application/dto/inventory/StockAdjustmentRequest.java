package com.cibertec.sistema_facturacion_g4.application.dto.inventory;

import lombok.Data;

@Data
public class StockAdjustmentRequest {
    private Long productId;
    private Integer newStock;
    private String reason;
    private String notes;
}