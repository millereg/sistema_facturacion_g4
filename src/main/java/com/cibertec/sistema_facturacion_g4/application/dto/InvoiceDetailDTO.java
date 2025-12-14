package com.cibertec.sistema_facturacion_g4.application.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InvoiceDetailDTO {
    private Long id;
    private Long invoiceId;
    private Long productId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal totalAmount;
}
