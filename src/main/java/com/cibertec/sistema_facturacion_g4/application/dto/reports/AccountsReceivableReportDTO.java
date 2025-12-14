package com.cibertec.sistema_facturacion_g4.application.dto.reports;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountsReceivableReportDTO {
    private String reportTitle;
    private String generatedDate;
    private String companyName;
    private BigDecimal totalReceivable;
    private BigDecimal overdueAmount;
    private int customerCount;
    private List<PendingInvoiceInfo> customers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingInvoiceInfo {
        private Long invoiceId;
        private String invoiceNumber;
        private String customerName;
        private String customerDocument;
        private String customerEmail;
        private String issueDate;
        private String dueDate;
        private BigDecimal amount;
        private int daysOverdue;
        private String status;
    }
}