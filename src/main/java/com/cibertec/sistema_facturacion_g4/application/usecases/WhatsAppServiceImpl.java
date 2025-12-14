package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.WhatsAppMessageDTO;
import com.cibertec.sistema_facturacion_g4.application.mapper.WhatsAppMessageMapper;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import com.cibertec.sistema_facturacion_g4.application.ports.WhatsAppService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.entities.Payment;
import com.cibertec.sistema_facturacion_g4.domain.entities.WhatsAppMessage;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.PaymentRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.WhatsAppMessageRepository;
import com.cibertec.sistema_facturacion_g4.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppServiceImpl implements WhatsAppService {

    private final WhatsAppMessageRepository whatsAppMessageRepository;
    private final WhatsAppMessageMapper whatsAppMessageMapper;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ConfigurationService configurationService;

    @Override
    @Transactional
    public WhatsAppMessageDTO sendInvoice(Long invoiceId, String phoneNumber) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        String message = String.format(
                "Hola %s,\n\n" +
                        "Se ha generado la factura %s-%s por un monto de %s %s.\n" +
                        "Fecha de vencimiento: %s\n\n" +
                        "Gracias por su preferencia.",
                invoice.getCustomerName(),
                invoice.getSeries(),
                invoice.getNumber(),
                invoice.getTotalAmount(),
                invoice.getTotalCurrency(),
                invoice.getDueDate());

        return sendCustomMessage(phoneNumber, message, "INVOICE");
    }

    @Override
    @Transactional
    public WhatsAppMessageDTO sendPaymentReminder(Long invoiceId, String phoneNumber) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        String message = String.format(
                "Estimado %s,\n\n" +
                        "Le recordamos que la factura %s-%s con vencimiento el %s aún está pendiente de pago.\n" +
                        "Monto: %s %s\n\n" +
                        "Por favor, regularice su pago a la brevedad.",
                invoice.getCustomerName(),
                invoice.getSeries(),
                invoice.getNumber(),
                invoice.getDueDate(),
                invoice.getTotalAmount(),
                invoice.getTotalCurrency());

        return sendCustomMessage(phoneNumber, message, "PAYMENT_REMINDER");
    }

    @Override
    @Transactional
    public WhatsAppMessageDTO sendPaymentConfirmation(Long paymentId, String phoneNumber) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        String message = String.format(
                "Estimado cliente,\n\n" +
                        "Confirmamos la recepción de su pago:\n" +
                        "Monto: %s %s\n" +
                        "Fecha: %s\n" +
                        "Método: %s\n\n" +
                        "Gracias por su pago.",
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentDate(),
                payment.getMethod());

        return sendCustomMessage(phoneNumber, message, "PAYMENT_CONFIRMATION");
    }

    @Override
    @Transactional
    public WhatsAppMessageDTO sendCustomMessage(String phoneNumber, String message, String messageType) {
        Long companyId = SecurityUtils.getCurrentCompanyId();

        WhatsAppMessage whatsAppMessage = new WhatsAppMessage();
        whatsAppMessage.setPhoneNumber(phoneNumber);
        whatsAppMessage.setMessage(message);
        whatsAppMessage.setMessageType(messageType);
        whatsAppMessage.setCompanyId(companyId);
        whatsAppMessage.setStatus(WhatsAppMessage.MessageStatus.PENDING);

        WhatsAppMessage saved = whatsAppMessageRepository.save(whatsAppMessage);

        SystemConfigurationDTO enabledConfig = configurationService.getConfigByKey("WHATSAPP_ENABLED");
        boolean whatsappEnabled = enabledConfig != null && "true".equalsIgnoreCase(enabledConfig.getValue());

        if (whatsappEnabled) {
            try {
                sendToWhatsAppAPI(companyId, phoneNumber, message);
                saved.setStatus(WhatsAppMessage.MessageStatus.SENT);
                saved.setSentAt(LocalDateTime.now());
            } catch (Exception e) {
                log.error("Error al enviar mensaje de WhatsApp: {}", e.getMessage());
                saved.setStatus(WhatsAppMessage.MessageStatus.FAILED);
                saved.setErrorMessage(e.getMessage());
            }
            whatsAppMessageRepository.save(saved);
        } else {
            log.info("WhatsApp deshabilitado para empresa {}. Mensaje registrado pero no enviado.", companyId);
        }

        return whatsAppMessageMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WhatsAppMessageDTO> getAllMessages() {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        return whatsAppMessageRepository.findByCompanyIdOrderByCreatedAtDesc(companyId)
                .stream()
                .map(whatsAppMessageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WhatsAppMessageDTO> getPendingMessages() {
        return whatsAppMessageRepository.findByStatus(WhatsAppMessage.MessageStatus.PENDING)
                .stream()
                .map(whatsAppMessageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processPendingMessages() {
        List<WhatsAppMessage> pendingMessages = whatsAppMessageRepository
                .findByStatus(WhatsAppMessage.MessageStatus.PENDING);

        for (WhatsAppMessage message : pendingMessages) {
            SystemConfigurationDTO enabledConfig = configurationService.getConfigByKey("WHATSAPP_ENABLED");
            boolean whatsappEnabled = enabledConfig != null && "true".equalsIgnoreCase(enabledConfig.getValue());

            if (!whatsappEnabled) {
                log.debug("WhatsApp deshabilitado para empresa {}. Saltando mensaje.", message.getCompanyId());
                continue;
            }

            try {
                sendToWhatsAppAPI(message.getCompanyId(), message.getPhoneNumber(), message.getMessage());
                message.setStatus(WhatsAppMessage.MessageStatus.SENT);
                message.setSentAt(LocalDateTime.now());
                log.info("Mensaje enviado exitosamente a {}", message.getPhoneNumber());
            } catch (Exception e) {
                log.error("Error al enviar mensaje a {}: {}", message.getPhoneNumber(), e.getMessage());
                message.setStatus(WhatsAppMessage.MessageStatus.FAILED);
                message.setErrorMessage(e.getMessage());
            }
            whatsAppMessageRepository.save(message);
        }
    }

    private void sendToWhatsAppAPI(Long companyId, String phoneNumber, String message) {
        SystemConfigurationDTO phoneIdConfig = configurationService.getConfigByKey("WHATSAPP_PHONE_NUMBER_ID");
        String phoneNumberId = phoneIdConfig != null ? phoneIdConfig.getValue() : null;
        
        SystemConfigurationDTO tokenConfig = configurationService.getConfigByKey("WHATSAPP_ACCESS_TOKEN");
        String accessToken = tokenConfig != null ? tokenConfig.getValue() : null;
        
        SystemConfigurationDTO versionConfig = configurationService.getConfigByKey("WHATSAPP_API_VERSION");
        String apiVersion = versionConfig != null ? versionConfig.getValue() : "v18.0";

        if (phoneNumberId == null || phoneNumberId.isEmpty()) {
            throw new RuntimeException("WhatsApp Phone Number ID no configurado para la empresa " + companyId);
        }

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("WhatsApp Access Token no configurado para la empresa " + companyId);
        }

        String whatsappApiUrl = String.format("https://graph.facebook.com/%s/%s/messages", apiVersion, phoneNumberId);

        RestTemplate restTemplate = new RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> requestBody = Map.of(
                "messaging_product", "whatsapp",
                "to", phoneNumber,
                "type", "text",
                "text", Map.of("body", message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(whatsappApiUrl, request, String.class);
        log.info("[Empresa {}] Respuesta de WhatsApp: {}", companyId, response.getBody());
    }
}
