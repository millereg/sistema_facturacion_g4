package com.cibertec.sistema_facturacion_g4.application.dto.pos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemRequest {
    private Long productId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}