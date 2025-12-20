package com.cibertec.sistema_facturacion_g4.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDTO {
    private Long id;
    private String number;
    private String series;
    private String type;
    private String status;
    private String issueDate;
    private String dueDate;
    private Long customerId;
    private String customerName;
    private String customerDocument;
    private Long userId;
    private String userName;
    private BigDecimal subtotal;
    private String subtotalCurrency;
    private BigDecimal taxAmount;
    private String taxCurrency;
    private BigDecimal discountAmount;
    private String discountCurrency;
    private BigDecimal totalAmount;
    private String totalCurrency;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
    private String paymentMethod;
    private String notes;
    private Long companyId;
    private Boolean active;
    private Boolean sentToSunat;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sunatSentAt;
    
    private String sunatResponseCode;
    private String sunatHash;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private List<InvoiceDetailDTO> details;
}
