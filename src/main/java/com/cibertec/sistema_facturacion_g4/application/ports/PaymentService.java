package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.PaymentDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {
    PaymentDTO createPayment(PaymentDTO paymentDTO);

    PaymentDTO getPaymentById(Long id);

    List<PaymentDTO> getAllPayments();

    List<PaymentDTO> getPaymentsByInvoice(Long invoiceId);

    List<PaymentDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    void deletePayment(Long id);
}
