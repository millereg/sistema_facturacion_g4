package com.cibertec.sistema_facturacion_g4.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppMessageDTO {
    private Long id;
    private String phoneNumber;
    private String message;
    private String status;
    private String messageType;
    private String entityType;
    private Long entityId;
    private Long companyId;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private String errorMessage;
    private LocalDateTime createdAt;
}
