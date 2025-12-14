package com.cibertec.sistema_facturacion_g4.shared.utils;

import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class TaxCalculator {
    private final ConfigurationService configurationService;
    private static final int SCALE = 2;
    
    public BigDecimal calculateIGV(BigDecimal subtotal) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        SystemConfigurationDTO igvConfig = configurationService.getConfigByKey("IGV_RATE");
        BigDecimal igvRate = igvConfig != null ? new BigDecimal(igvConfig.getValue()) : new BigDecimal("0.18");
        return subtotal.multiply(igvRate).setScale(SCALE, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal tax, BigDecimal discount) {
        BigDecimal total = subtotal != null ? subtotal : BigDecimal.ZERO;
        if (tax != null) {
            total = total.add(tax);
        }
        if (discount != null) {
            total = total.subtract(discount);
        }
        return total.setScale(SCALE, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateSubtotal(BigDecimal unitPrice, BigDecimal quantity) {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(quantity).setScale(SCALE, RoundingMode.HALF_UP);
    }
    
    public BigDecimal applyDiscount(BigDecimal amount, BigDecimal discountPercentage) {
        if (amount == null || discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(discountPercentage).divide(new BigDecimal("100"), SCALE, RoundingMode.HALF_UP);
    }
}
