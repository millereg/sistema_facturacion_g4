package com.cibertec.sistema_facturacion_g4.application.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentDTO {
    private Long id;
    private Long invoiceId;
    private String invoiceCode;
    private BigDecimal amount;
    private String currency;
    private String method;
    private String paymentDate;
    private Long companyId;
    private String createdAt;
    private String updatedAt;
}
