package com.cibertec.sistema_facturacion_g4.application.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportDTO {
    private Long companyId;
    private String companyName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalInvoices;
    private BigDecimal totalSales;
    private BigDecimal totalTax;
    private BigDecimal averageTicket;
    private String currency;
}
