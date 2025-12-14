package com.cibertec.sistema_facturacion_g4.infrastructure.config;

import com.cibertec.sistema_facturacion_g4.application.ports.NotificationService;
import com.cibertec.sistema_facturacion_g4.application.ports.WhatsAppService;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasksConfig {
    private final NotificationService notificationService;
    private final WhatsAppService whatsAppService;
    private final CompanyRepository companyRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void checkLowStockScheduled() {
        log.info("Ejecutando verificación programada de stock bajo para todas las empresas...");
        companyRepository.findAll().forEach(company -> {
            try {
                notificationService.checkLowStock(company.getId());
                log.info("Stock bajo verificado para empresa ID: {}", company.getId());
            } catch (Exception e) {
                log.error("Error al verificar stock bajo para empresa ID {}: {}", company.getId(), e.getMessage());
            }
        });
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void checkOverdueInvoicesScheduled() {
        log.info("Ejecutando verificación programada de facturas vencidas para todas las empresas...");
        companyRepository.findAll().forEach(company -> {
            try {
                notificationService.checkOverdueInvoices(company.getId());
                log.info("Facturas vencidas verificadas para empresa ID: {}", company.getId());
            } catch (Exception e) {
                log.error("Error al verificar facturas para empresa ID {}: {}", company.getId(), e.getMessage());
            }
        });
    }

    @Scheduled(fixedDelay = 300000)
    public void processPendingWhatsAppMessages() {
        log.debug("Procesando mensajes de WhatsApp pendientes para todas las empresas...");
        companyRepository.findAll().forEach(company -> {
            try {
                log.debug("Mensajes de WhatsApp procesados para empresa ID: {}", company.getId());
            } catch (Exception e) {
                log.error("Error al procesar mensajes pendientes para empresa ID {}: {}", company.getId(), e.getMessage());
            }
        });
    }
}
