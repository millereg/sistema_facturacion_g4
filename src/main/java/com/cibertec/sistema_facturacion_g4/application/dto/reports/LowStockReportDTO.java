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
public class LowStockReportDTO {
    
    private String reportTitle;
    private String generatedDate;
    private String companyName;
    private int totalProducts;
    private int lowStockProducts;
    private List<ProductStockInfo> products;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductStockInfo {
        private Long productId;
        private Long supplierId;
        private String supplierName;
        private String productCode;
        private String productName;
        private String category;
        private int currentStock;
        private int minimumStock;
        private String status;
        private BigDecimal unitPrice;
        private BigDecimal totalValue;
    }
}