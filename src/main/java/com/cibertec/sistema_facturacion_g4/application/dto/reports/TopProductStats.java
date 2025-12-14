package com.cibertec.sistema_facturacion_g4.application.dto.reports;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopProductStats {
    private Long productId;
    private String productCode;
    private String productName;
    private int quantitySold = 0;
    private BigDecimal revenue = BigDecimal.ZERO;
    private BigDecimal unitPrice = BigDecimal.ZERO;
}
