package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.NotificationDTO;
import com.cibertec.sistema_facturacion_g4.domain.entities.Notification;

import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(Notification.NotificationType type, String title, String message,
            Notification.Severity severity, Long entityId, String entityType, Long userId);

    List<NotificationDTO> getAllNotifications();

    List<NotificationDTO> getUnreadNotifications();

    List<NotificationDTO> getUserNotifications(Long userId);

    NotificationDTO markAsRead(Long id);

    void markAllAsRead();

    Long getUnreadCount();

    void checkLowStock();

    void checkLowStock(Long companyId);

    void checkOverdueInvoices();

    void checkOverdueInvoices(Long companyId);
}
