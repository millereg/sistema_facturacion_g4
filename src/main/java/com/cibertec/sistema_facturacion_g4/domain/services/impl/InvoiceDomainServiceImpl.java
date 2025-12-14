package com.cibertec.sistema_facturacion_g4.domain.services.impl;

import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.entities.InvoiceDetail;
import com.cibertec.sistema_facturacion_g4.domain.services.InvoiceDomainService;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.InsufficientStockException;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.InvalidOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceDomainServiceImpl implements InvoiceDomainService {

    private final ConfigurationService configurationService;
    private static final int CORRELATIVE_LENGTH = 8;

    @Override
    public void validateProductStock(Product product, BigDecimal quantity) {
        if (product.getStock() == null || product.getStock() < quantity.intValue()) {
            throw new InsufficientStockException(
                    "Stock insuficiente para: " + product.getName() +
                            ". Disponible: " + product.getStock() +
                            ", Solicitado: " + quantity.intValue());
        }
    }

    @Override
    public void validateProductIsActive(Product product) {
        if (!product.getActive()) {
            throw new InvalidOperationException("Producto inactivo: " + product.getName());
        }
    }

    @Override
    public String generateInvoiceNumber(String series, Long lastCorrelative) {
        long nextCorrelative = (lastCorrelative == null) ? 1 : lastCorrelative + 1;
        return series + "-" + String.format("%0" + CORRELATIVE_LENGTH + "d", nextCorrelative);
    }

    @Override
    public InvoiceTotals calculateInvoiceTotals(List<InvoiceDetail> details) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (InvoiceDetail detail : details) {
            BigDecimal quantity = new BigDecimal(detail.getQuantity() != null ? detail.getQuantity() : 0);
            BigDecimal lineSubtotal = detail.getUnitPrice()
                    .multiply(quantity)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal lineDiscount = detail.getDiscount() != null
                    ? detail.getDiscount()
                    : BigDecimal.ZERO;

            BigDecimal lineTotal = lineSubtotal.subtract(lineDiscount);
            SystemConfigurationDTO igvConfig = configurationService.getConfigByKey("IGV_RATE");
            BigDecimal igvRate = igvConfig != null ? new BigDecimal(igvConfig.getValue()) : new BigDecimal("0.18");
            BigDecimal lineTax = lineTotal.multiply(igvRate).setScale(2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(lineSubtotal);
            totalDiscount = totalDiscount.add(lineDiscount);
            totalTax = totalTax.add(lineTax);
        }

        BigDecimal totalAmount = subtotal.subtract(totalDiscount).add(totalTax);

        return new InvoiceTotals(subtotal, totalTax, totalDiscount, totalAmount);
    }

    @Override
    public boolean canBeCancelled(Invoice invoice) {
        if (invoice == null) {
            return false;
        }

        return invoice.getStatus() == Invoice.InvoiceStatus.ISSUED
                || invoice.getStatus() == Invoice.InvoiceStatus.PAID;
    }
}
