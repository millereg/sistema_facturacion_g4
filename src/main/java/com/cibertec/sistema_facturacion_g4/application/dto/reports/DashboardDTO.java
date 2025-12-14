package com.cibertec.sistema_facturacion_g4.application.dto.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private Long companyId;
    private String companyName;
    private BigDecimal salesToday;
    private BigDecimal salesThisMonth;
    private Integer invoicesThisMonth;
    private Integer totalProducts;
    private Integer lowStockProducts;
    private Integer totalCustomers;
    private Integer activeCustomers;
    private BigDecimal pendingPayments;
    private String currency;
}
