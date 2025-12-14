package com.cibertec.sistema_facturacion_g4.domain.services.impl;

import com.cibertec.sistema_facturacion_g4.domain.entities.Payment;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.services.PaymentDomainService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentDomainServiceImpl implements PaymentDomainService {
    @Override
    public BigDecimal calculateOutstandingBalance(Invoice invoice, List<Payment> payments) {
        BigDecimal totalPaid = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return invoice.getTotalAmount().subtract(totalPaid);
    }

    @Override
    public boolean registerPayment(Invoice invoice, Payment payment, List<Payment> existingPayments) {
        BigDecimal balance = calculateOutstandingBalance(invoice, existingPayments);
        if (payment.getAmount().compareTo(balance) > 0) {
            return false;
        }
        existingPayments.add(payment);
        return true;
    }

    @Override
    public boolean validatePaymentMethod(String method) {
        return "EFECTIVO".equalsIgnoreCase(method) || "TARJETA".equalsIgnoreCase(method)
                || "TRANSFERENCIA".equalsIgnoreCase(method);
    }
}
