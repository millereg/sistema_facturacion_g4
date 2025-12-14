package com.cibertec.sistema_facturacion_g4.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String severity;
    private Boolean isRead;
    private String entityType;
    private Long entityId;
    private Long userId;
    private Long companyId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
