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
public class TopProductsReportDTO {
    private String reportTitle;
    private String generatedDate;
    private String companyName;
    private String period;
    private int totalProductsSold;
    private BigDecimal totalRevenue;
    private List<TopProductInfo> topProducts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductInfo {
        private Long productId;
        private String productCode;
        private String productName;
        private String category;
        private int quantitySold;
        private BigDecimal unitPrice;
        private BigDecimal totalRevenue;
        private int ranking;
        private double percentageOfTotal;
    }
}