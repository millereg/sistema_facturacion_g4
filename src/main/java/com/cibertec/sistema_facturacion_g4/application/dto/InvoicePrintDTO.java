package com.cibertec.sistema_facturacion_g4.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePrintDTO {
    private CompanyInfo company;
    private DocumentInfo document;
    private CustomerInfo customer;
    private List<InvoiceDetailInfo> details;
    private TotalsInfo totals;
    private AdditionalInfo additional;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyInfo {
        private String businessName;
        private String tradeName;
        private String ruc;
        private String address;
        private String district;
        private String province;
        private String department;
        private String country;
        private String phone;
        private String email;
        private String website;
        private String logoUrl;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentInfo {
        private String type;
        private String series;
        private String number;
        private String fullNumber;
        private LocalDateTime issueDate;
        private LocalDateTime dueDate;
        private String status;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String documentType;
        private String documentNumber;
        private String fullName;
        private String address;
        private String phone;
        private String email;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceDetailInfo {
        private Integer item;
        private String code;
        private String description;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal total;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalsInfo {
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal taxRate;
        private BigDecimal taxAmount;
        private BigDecimal total;
        private String currency;
        private String totalInWords;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalInfo {
        private String paymentMethod;
        private String userName;
        private String notes;
        private String qrCode;
        private String hash;
    }
}
