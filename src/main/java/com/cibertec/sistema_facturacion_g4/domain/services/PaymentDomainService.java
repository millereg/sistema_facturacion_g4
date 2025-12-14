package com.cibertec.sistema_facturacion_g4.domain.services;

import com.cibertec.sistema_facturacion_g4.domain.entities.Payment;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentDomainService {
    BigDecimal calculateOutstandingBalance(Invoice invoice, List<Payment> payments);

    boolean registerPayment(Invoice invoice, Payment payment, List<Payment> existingPayments);

    boolean validatePaymentMethod(String method);
}
