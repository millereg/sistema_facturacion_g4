package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.NotificationDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import com.cibertec.sistema_facturacion_g4.application.mapper.NotificationMapper;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import com.cibertec.sistema_facturacion_g4.application.ports.NotificationService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.entities.Notification;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.NotificationRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ProductRepository;
import com.cibertec.sistema_facturacion_g4.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
        private final NotificationRepository notificationRepository;
        private final NotificationMapper notificationMapper;
        private final ConfigurationService configurationService;
        private final ProductRepository productRepository;
        private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public NotificationDTO createNotification(Notification.NotificationType type, String title, String message,
                    Notification.Severity severity, Long entityId, String entityType, Long userId) {
        Long companyId = SecurityUtils.getCurrentCompanyId();
        return createNotificationWithCompany(type, title, message, severity, entityId, entityType, userId, companyId);
    }

    @Transactional
    public NotificationDTO createNotificationWithCompany(Notification.NotificationType type, String title, String message,
                    Notification.Severity severity, Long entityId, String entityType, Long userId, Long companyId) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setSeverity(severity);
        notification.setEntityId(entityId);
        notification.setEntityType(entityType);
        notification.setUserId(userId);
        notification.setCompanyId(companyId);
        notification.setIsRead(false);

        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toDTO(saved);
    }        @Override
        @Transactional(readOnly = true)
        public List<NotificationDTO> getAllNotifications() {
                Long companyId = SecurityUtils.getCurrentCompanyId();
                return notificationRepository.findByCompanyIdOrderByCreatedAtDesc(companyId)
                                .stream()
                                .map(notificationMapper::toDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<NotificationDTO> getUnreadNotifications() {
                Long companyId = SecurityUtils.getCurrentCompanyId();
                return notificationRepository.findByCompanyIdAndIsReadOrderByCreatedAtDesc(companyId, false)
                                .stream()
                                .map(notificationMapper::toDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<NotificationDTO> getUserNotifications(Long userId) {
                return notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false)
                                .stream()
                                .map(notificationMapper::toDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public NotificationDTO markAsRead(Long id) {
                Notification notification = notificationRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
                Notification saved = notificationRepository.save(notification);
                return notificationMapper.toDTO(saved);
        }

        @Override
        @Transactional
        public void markAllAsRead() {
                Long companyId = SecurityUtils.getCurrentCompanyId();
                List<Notification> notifications = notificationRepository
                                .findByCompanyIdAndIsReadOrderByCreatedAtDesc(companyId, false);
                notifications.forEach(notification -> {
                        notification.setIsRead(true);
                        notification.setReadAt(LocalDateTime.now());
                });
                notificationRepository.saveAll(notifications);
        }

        @Override
        @Transactional(readOnly = true)
        public Long getUnreadCount() {
                Long companyId = SecurityUtils.getCurrentCompanyId();
                return notificationRepository.countByCompanyIdAndIsRead(companyId, false);
        }

        @Override
        @Transactional
        public void checkLowStock() {
                Long companyId = SecurityUtils.getCurrentCompanyId();
                checkLowStock(companyId);
        }

        @Override
        @Transactional
        public void checkLowStock(Long companyId) {
                SystemConfigurationDTO thresholdConfig = configurationService.getConfigByKey("LOW_STOCK_THRESHOLD", companyId);
                Integer threshold = thresholdConfig != null ? Integer.parseInt(thresholdConfig.getValue()) : 10;

                List<Product> lowStockProducts = productRepository.findByCompanyId(companyId)
                                .stream()
                                .filter(p -> p.getStock() != null && p.getStock().intValue() <= threshold)
                                .collect(Collectors.toList());

                for (Product product : lowStockProducts) {
                        boolean hasRecentNotification = notificationRepository.findByCompanyIdAndCreatedAtBetween(
                                        companyId,
                                        LocalDateTime.now().minusDays(1),
                                        LocalDateTime.now()).stream()
                                        .anyMatch(n -> n.getType() == Notification.NotificationType.LOW_STOCK &&
                                                        n.getEntityId().equals(product.getId()));

                        if (!hasRecentNotification) {
                                createNotificationWithCompany(
                                                Notification.NotificationType.LOW_STOCK,
                                                "Stock Bajo",
                                                String.format("El producto %s tiene stock bajo: %s unidades",
                                                                product.getName(), product.getStock()),
                                                Notification.Severity.WARNING,
                                                product.getId(),
                                                "PRODUCT",
                                                null,
                                                companyId);
                        }
                }
        }

        @Override
        @Transactional
        public void checkOverdueInvoices() {
                Long companyId = SecurityUtils.getCurrentCompanyId();
                checkOverdueInvoices(companyId);
        }

        @Override
        @Transactional
        public void checkOverdueInvoices(Long companyId) {
                List<Invoice> overdueInvoices = invoiceRepository.findByCompanyId(companyId)
                                .stream()
                                .filter(i -> i.getDueDate() != null &&
                                                i.getDueDate().isBefore(LocalDateTime.now()) &&
                                                i.getStatus() != Invoice.InvoiceStatus.PAID)
                                .collect(Collectors.toList());

                for (Invoice invoice : overdueInvoices) {
                        boolean hasRecentNotification = notificationRepository.findByCompanyIdAndCreatedAtBetween(
                                        companyId,
                                        LocalDateTime.now().minusDays(1),
                                        LocalDateTime.now()).stream()
                                        .anyMatch(n -> n.getType() == Notification.NotificationType.INVOICE_OVERDUE &&
                                                        n.getEntityId().equals(invoice.getId()));

                        if (!hasRecentNotification) {
                                long daysOverdue = java.time.Duration.between(invoice.getDueDate(), LocalDateTime.now())
                                                .toDays();
                                createNotificationWithCompany(
                                                Notification.NotificationType.INVOICE_OVERDUE,
                                                "Factura Vencida",
                                                String.format("La factura %s-%s está vencida hace %d días. Cliente: %s",
                                                                invoice.getSeries(), invoice.getNumber(), daysOverdue,
                                                                invoice.getCustomerName()),
                                                Notification.Severity.ERROR,
                                                invoice.getId(),
                                                "INVOICE",
                                                null,
                                                companyId);
                        }
                }
        }
}
