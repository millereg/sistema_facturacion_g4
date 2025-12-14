package com.cibertec.sistema_facturacion_g4.infrastructure.email;

import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendInvoiceEmail(String to, Invoice invoice) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(buildSubject(invoice));
        message.setText(buildBody(invoice));
        try {
            mailSender.send(message);
            log.info("Correo de factura {} enviado a {}", invoice.getId(), to);
        } catch (Exception ex) {
            log.error("No se pudo enviar correo de factura {} a {}: {}", invoice.getId(), to, ex.getMessage());
            throw new RuntimeException("No se pudo enviar el correo: " + ex.getMessage(), ex);
        }
    }

    private String buildSubject(Invoice invoice) {
        String serie = invoice.getSeries() != null ? invoice.getSeries() : "";
        String number = invoice.getNumber() != null ? invoice.getNumber() : "";
        return "Tu comprobante " + serie + "-" + number;
    }

    private String buildBody(Invoice invoice) {
        StringBuilder body = new StringBuilder();
        body.append("Hola, adjuntamos los datos de tu comprobante.\n\n");
        body.append("Serie-Número: ").append(invoice.getSeries()).append("-").append(invoice.getNumber()).append("\n");
        body.append("Fecha emisión: ").append(invoice.getIssueDate()).append("\n");
        body.append("Total: ").append(invoice.getTotalAmount()).append(" ").append(invoice.getTotalCurrency()).append("\n\n");
        body.append("Gracias por su compra.");
        return body.toString();
    }
}
