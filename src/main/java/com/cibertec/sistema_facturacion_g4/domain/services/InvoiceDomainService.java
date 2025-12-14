package com.cibertec.sistema_facturacion_g4.domain.services;

import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.entities.InvoiceDetail;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import java.math.BigDecimal;
import java.util.List;

public interface InvoiceDomainService {
    void validateProductStock(Product product, BigDecimal quantity);

    void validateProductIsActive(Product product);

    String generateInvoiceNumber(String series, Long lastCorrelative);

    InvoiceTotals calculateInvoiceTotals(List<InvoiceDetail> details);

    boolean canBeCancelled(Invoice invoice);

    class InvoiceTotals {
        private final BigDecimal subtotal;
        private final BigDecimal taxAmount;
        private final BigDecimal discountAmount;
        private final BigDecimal totalAmount;

        public InvoiceTotals(BigDecimal subtotal, BigDecimal taxAmount,
                BigDecimal discountAmount, BigDecimal totalAmount) {
            this.subtotal = subtotal;
            this.taxAmount = taxAmount;
            this.discountAmount = discountAmount;
            this.totalAmount = totalAmount;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public BigDecimal getTaxAmount() {
            return taxAmount;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }
}
