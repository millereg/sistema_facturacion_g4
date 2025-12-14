package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.ai.*;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ProductRepository;
import com.cibertec.sistema_facturacion_g4.domain.services.AIAssistantService;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAssistantServiceImpl {

    @Qualifier("aiAssistantDomainService")
    private final AIAssistantService aiAssistantService;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;

    public AIResponse generateProductDescription(ProductDescriptionRequest request) {
        try {
            log.info("Solicitando descripción para: {}", request.getProductName());

            String description = aiAssistantService.generateProductDescription(
                    request.getProductName(),
                    request.getCategory());

            return AIResponse.success(description);

        } catch (Exception e) {
            log.error("Error al generar descripción: {}", e.getMessage());
            return AIResponse.error("No se pudo generar la descripción. " + e.getMessage());
        }
    }

    public AIResponse generateProductPrice(ProductPriceRequest request) {
        try {
            log.info("Solicitando precio para: {}", request.getProductName());

            String price = aiAssistantService.generateProductPrice(
                    request.getProductName(),
                    request.getCategory(),
                    request.getDescription());

            return AIResponse.success(price);

        } catch (Exception e) {
            log.error("Error al generar precio: {}", e.getMessage());
            return AIResponse.error("No se pudo generar el precio. " + e.getMessage());
        }
    }

    public AIResponse suggestDiscount(DiscountSuggestionRequest request) {
        try {
            log.info("Solicitando sugerencia de descuento para producto ID: {}", request.getProductId());

            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Producto no encontrado con ID: " + request.getProductId()));

            LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
            Double averageSales = calculateAverageMonthlySales(product.getId(), threeMonthsAgo);

            String suggestion = aiAssistantService.suggestDiscount(
                    product,
                    averageSales,
                    product.getStock());

            return AIResponse.success(suggestion);

        } catch (EntityNotFoundException e) {
            log.error("Producto no encontrado: {}", e.getMessage());
            return AIResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("Error al sugerir descuento: {}", e.getMessage());
            return AIResponse.error("No se pudo generar la sugerencia. " + e.getMessage());
        }
    }

    public AIResponse analyzeSalesTrends(SalesTrendRequest request) {
        try {
            log.info("Analizando tendencias del {} al {}", request.getStartDate(), request.getEndDate());

            if (request.getStartDate().isAfter(request.getEndDate())) {
                return AIResponse.error("La fecha de inicio debe ser anterior a la fecha de fin");
            }

            List<Invoice> invoices = invoiceRepository.findByIssueDateBetween(
                    request.getStartDate().atStartOfDay(),
                    request.getEndDate().atTime(23, 59, 59));

            if (invoices.isEmpty()) {
                return AIResponse.error("No hay datos de ventas para el periodo seleccionado");
            }

            Double totalSales = invoices.stream()
                    .filter(invoice -> invoice.getTotalAmount() != null)
                    .mapToDouble(invoice -> invoice.getTotalAmount().doubleValue())
                    .sum();

            String topProducts = getTopProducts(invoices);

            String analysis = aiAssistantService.analyzeSalesTrends(
                    request.getStartDate(),
                    request.getEndDate(),
                    totalSales,
                    topProducts);

            return AIResponse.success(analysis);

        } catch (Exception e) {
            log.error("Error al analizar tendencias: {}", e.getMessage());
            return AIResponse.error("No se pudo generar el análisis. " + e.getMessage());
        }
    }

    private Double calculateAverageMonthlySales(Long productId, LocalDate since) {
        List<Invoice> invoices = invoiceRepository.findByIssueDateBetween(
                since.atStartOfDay(),
                LocalDate.now().atTime(23, 59, 59));

        int totalQuantity = invoices.stream()
                .filter(invoice -> invoice.getDetails() != null)
                .flatMap(invoice -> invoice.getDetails().stream())
                .filter(detail -> detail.getProductId() != null && detail.getProductId().equals(productId))
                .mapToInt(detail -> detail.getQuantity() != null ? detail.getQuantity().intValue() : 0)
                .sum();

        long monthsDiff = java.time.temporal.ChronoUnit.MONTHS.between(since, LocalDate.now());
        if (monthsDiff == 0)
            monthsDiff = 1;

        return (double) totalQuantity / monthsDiff;
    }

    private String getTopProducts(List<Invoice> invoices) {
        Map<String, Integer> productSales = new HashMap<>();

        invoices.forEach(invoice -> {
            if (invoice.getDetails() != null) {
                invoice.getDetails().forEach(detail -> {
                    String productName = detail.getProductName();
                    if (productName != null && detail.getQuantity() != null) {
                        productSales.merge(productName, detail.getQuantity().intValue(), Integer::sum);
                    }
                });
            }
        });

        if (productSales.isEmpty()) {
            return "No se encontraron productos en el periodo seleccionado";
        }
        
        return productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }
}
