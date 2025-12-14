package com.cibertec.sistema_facturacion_g4.application.dto.inventory;

import lombok.Data;

@Data
public class StockReportDTO {
    private Long productId;
    private String productCode;
    private String productName;
    private Integer currentStock;
    private Integer minimumStock;
    private Boolean isLowStock;
    private Integer suggestedReorder;
    private String categoryName;
    private String supplierName;
}