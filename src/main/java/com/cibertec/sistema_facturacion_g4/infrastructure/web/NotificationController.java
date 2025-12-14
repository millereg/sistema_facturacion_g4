package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.NotificationDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Gestión de notificaciones del sistema")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Listar todas las notificaciones")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/unread")
    @Operation(summary = "Listar notificaciones no leídas")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Contar notificaciones no leídas")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getUnreadCount());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar notificaciones de un usuario")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Marcar todas las notificaciones como leídas")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-low-stock")
    @Operation(summary = "Verificar productos con stock bajo (manual)")
    public ResponseEntity<Void> checkLowStock() {
        notificationService.checkLowStock();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check-overdue-invoices")
    @Operation(summary = "Verificar facturas vencidas (manual)")
    public ResponseEntity<Void> checkOverdueInvoices() {
        notificationService.checkOverdueInvoices();
        return ResponseEntity.ok().build();
    }
}
