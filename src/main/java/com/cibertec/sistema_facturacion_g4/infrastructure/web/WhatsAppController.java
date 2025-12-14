package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.WhatsAppMessageDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import com.cibertec.sistema_facturacion_g4.application.ports.WhatsAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
@Tag(name = "WhatsApp", description = "Gestión de mensajes de WhatsApp Business")
public class WhatsAppController {
    private final WhatsAppService whatsAppService;
    private final ConfigurationService configurationService;

    @PostMapping("/send-invoice")
    @Operation(summary = "Enviar factura por WhatsApp")
    public ResponseEntity<WhatsAppMessageDTO> sendInvoice(@RequestBody Map<String, Object> request) {
        Long invoiceId = Long.valueOf(request.get("invoiceId").toString());
        String phoneNumber = request.get("phoneNumber").toString();
        return ResponseEntity.ok(whatsAppService.sendInvoice(invoiceId, phoneNumber));
    }

    @PostMapping("/send-payment-reminder")
    @Operation(summary = "Enviar recordatorio de pago por WhatsApp")
    public ResponseEntity<WhatsAppMessageDTO> sendPaymentReminder(@RequestBody Map<String, Object> request) {
        Long invoiceId = Long.valueOf(request.get("invoiceId").toString());
        String phoneNumber = request.get("phoneNumber").toString();
        return ResponseEntity.ok(whatsAppService.sendPaymentReminder(invoiceId, phoneNumber));
    }

    @PostMapping("/send-payment-confirmation")
    @Operation(summary = "Enviar confirmación de pago por WhatsApp")
    public ResponseEntity<WhatsAppMessageDTO> sendPaymentConfirmation(@RequestBody Map<String, Object> request) {
        Long paymentId = Long.valueOf(request.get("paymentId").toString());
        String phoneNumber = request.get("phoneNumber").toString();
        return ResponseEntity.ok(whatsAppService.sendPaymentConfirmation(paymentId, phoneNumber));
    }

    @PostMapping("/send-custom")
    @Operation(summary = "Enviar mensaje personalizado por WhatsApp")
    public ResponseEntity<WhatsAppMessageDTO> sendCustomMessage(@RequestBody Map<String, Object> request) {
        String phoneNumber = request.get("phoneNumber").toString();
        String message = request.get("message").toString();
        String messageType = request.getOrDefault("messageType", "CUSTOM").toString();
        return ResponseEntity.ok(whatsAppService.sendCustomMessage(phoneNumber, message, messageType));
    }

    @GetMapping("/messages")
    @Operation(summary = "Listar todos los mensajes de WhatsApp")
    public ResponseEntity<List<WhatsAppMessageDTO>> getAllMessages() {
        return ResponseEntity.ok(whatsAppService.getAllMessages());
    }

    @GetMapping("/messages/pending")
    @Operation(summary = "Listar mensajes pendientes de envío")
    public ResponseEntity<List<WhatsAppMessageDTO>> getPendingMessages() {
        return ResponseEntity.ok(whatsAppService.getPendingMessages());
    }

    @PostMapping("/process-pending")
    @Operation(summary = "Procesar mensajes pendientes (manual)")
    public ResponseEntity<Void> processPendingMessages() {
        whatsAppService.processPendingMessages();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/config")
    @Operation(summary = "Obtener configuración de WhatsApp de la empresa")
    public ResponseEntity<Map<String, String>> getWhatsAppConfig() {
        Map<String, String> config = new HashMap<>();
        
        SystemConfigurationDTO enabled = configurationService.getConfigByKey("WHATSAPP_ENABLED");
        config.put("enabled", enabled != null ? enabled.getValue() : "false");
        
        SystemConfigurationDTO phoneId = configurationService.getConfigByKey("WHATSAPP_PHONE_NUMBER_ID");
        config.put("phoneNumberId", phoneId != null ? phoneId.getValue() : "");
        
        SystemConfigurationDTO apiVer = configurationService.getConfigByKey("WHATSAPP_API_VERSION");
        config.put("apiVersion", apiVer != null ? apiVer.getValue() : "v18.0");
        
        SystemConfigurationDTO tokenConfig = configurationService.getConfigByKey("WHATSAPP_ACCESS_TOKEN");
        String token = tokenConfig != null ? tokenConfig.getValue() : null;
        if (token != null && !token.isEmpty()) {
            config.put("hasToken", "true");
            config.put("tokenPreview", token.substring(0, Math.min(10, token.length())) + "...");
        } else {
            config.put("hasToken", "false");
        }
        return ResponseEntity.ok(config);
    }

    @PutMapping("/config")
    @Operation(summary = "Actualizar configuración de WhatsApp de la empresa")
    public ResponseEntity<Map<String, String>> updateWhatsAppConfig(@RequestBody Map<String, String> config) {
        if (config.containsKey("enabled")) {
            SystemConfigurationDTO dto = new SystemConfigurationDTO();
            dto.setKey("WHATSAPP_ENABLED");
            dto.setValue(config.get("enabled"));
            dto.setDescription("Habilitar/deshabilitar WhatsApp");
            configurationService.saveConfiguration(dto);
        }
        if (config.containsKey("phoneNumberId")) {
            SystemConfigurationDTO dto = new SystemConfigurationDTO();
            dto.setKey("WHATSAPP_PHONE_NUMBER_ID");
            dto.setValue(config.get("phoneNumberId"));
            dto.setDescription("ID del número de teléfono de WhatsApp Business");
            configurationService.saveConfiguration(dto);
        }
        if (config.containsKey("accessToken")) {
            SystemConfigurationDTO dto = new SystemConfigurationDTO();
            dto.setKey("WHATSAPP_ACCESS_TOKEN");
            dto.setValue(config.get("accessToken"));
            dto.setDescription("Token de acceso de WhatsApp Business API");
            configurationService.saveConfiguration(dto);
        }
        if (config.containsKey("apiVersion")) {
            SystemConfigurationDTO dto = new SystemConfigurationDTO();
            dto.setKey("WHATSAPP_API_VERSION");
            dto.setValue(config.get("apiVersion"));
            dto.setDescription("Versión de la API de WhatsApp");
            configurationService.saveConfiguration(dto);
        }
        
        return ResponseEntity.ok(Map.of("message", "Configuración de WhatsApp actualizada correctamente"));
    }

    @PostMapping("/config/test")
    @Operation(summary = "Probar configuración de WhatsApp (envío de prueba)")
    public ResponseEntity<Map<String, String>> testWhatsAppConfig(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "phoneNumber es requerido"));
        }
        
        SystemConfigurationDTO enabledConfig = configurationService.getConfigByKey("WHATSAPP_ENABLED");
        String enabled = enabledConfig != null ? enabledConfig.getValue() : "false";
        if (!"true".equalsIgnoreCase(enabled)) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "WhatsApp está deshabilitado",
                "solution", "Activa WhatsApp en la configuración primero"
            ));
        }
        
        SystemConfigurationDTO phoneIdConfig = configurationService.getConfigByKey("WHATSAPP_PHONE_NUMBER_ID");
        String phoneNumberId = phoneIdConfig != null ? phoneIdConfig.getValue() : null;
        
        SystemConfigurationDTO tokenConfig = configurationService.getConfigByKey("WHATSAPP_ACCESS_TOKEN");
        String accessToken = tokenConfig != null ? tokenConfig.getValue() : null;
        
        if (phoneNumberId == null || phoneNumberId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Phone Number ID no configurado",
                "solution", "Configura WHATSAPP_PHONE_NUMBER_ID en los parámetros del sistema"
            ));
        }
        
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Access Token no configurado",
                "solution", "Configura WHATSAPP_ACCESS_TOKEN en los parámetros del sistema"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "message", "Configuración válida",
            "phoneNumberId", phoneNumberId,
            "hasToken", "true",
            "testNumber", phoneNumber,
            "note", "Integración real activada - los mensajes se enviarán a WhatsApp"
        ));
    }
}
