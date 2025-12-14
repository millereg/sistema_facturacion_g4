package com.cibertec.sistema_facturacion_g4.application.dto.inventory;

import lombok.Data;

@Data
public class StockMovementDTO {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private String movementType;
    private String reason;
    private String notes;
    private String movementDate;
    private Long userId;
    private String userName;
}