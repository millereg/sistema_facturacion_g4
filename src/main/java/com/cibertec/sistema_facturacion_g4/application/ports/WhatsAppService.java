package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.WhatsAppMessageDTO;

import java.util.List;

public interface WhatsAppService {
    WhatsAppMessageDTO sendInvoice(Long invoiceId, String phoneNumber);

    WhatsAppMessageDTO sendPaymentReminder(Long invoiceId, String phoneNumber);

    WhatsAppMessageDTO sendPaymentConfirmation(Long paymentId, String phoneNumber);

    WhatsAppMessageDTO sendCustomMessage(String phoneNumber, String message, String messageType);

    List<WhatsAppMessageDTO> getAllMessages();

    List<WhatsAppMessageDTO> getPendingMessages();

    void processPendingMessages();
}
