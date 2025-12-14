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
public class InventoryReportDTO {
    private Long companyId;
    private String companyName;
    private Integer totalProducts;
    private Integer lowStockProducts;
    private BigDecimal totalInventoryValue;
    private String currency;
}
