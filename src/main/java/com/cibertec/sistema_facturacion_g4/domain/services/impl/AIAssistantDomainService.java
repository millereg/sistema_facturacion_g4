package com.cibertec.sistema_facturacion_g4.domain.services.impl;

import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.services.AIAssistantService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service("aiAssistantDomainService")
@RequiredArgsConstructor
@Slf4j
public class AIAssistantDomainService implements AIAssistantService {
    private final ChatClient chatClient;

    @Override
    public String generateProductDescription(String productName, String category) {
        String prompt = String.format(
                """
                        Genera una descripción simple para inventario POS (máximo 80 palabras):

                        Producto: %s
                        Categoría: %s

                        La descripción debe ser:
                        - Simple y directa
                        - Solo características básicas
                        - Sin lenguaje comercial
                        - Apropiada para sistema de inventario

                        Responde SOLO con la descripción, sin encabezados.
                        """,
                productName,
                category);

        try {
            log.info("Generando descripción para producto: {}", productName);
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return response.trim();

        } catch (Exception e) {
            log.error("Error al generar descripción con IA: {}, generando descripción básica", e.getMessage());
            return generateBasicDescription(productName, category);
        }
    }

    private String generateBasicDescription(String productName, String category) {
        return String.format("Producto de calidad %s en la categoría %s. " +
                "Disponible para venta inmediata con garantía incluida.",
                productName, category != null ? category : "general");
    }

    @Override
    public String generateProductPrice(String productName, String category, String description) {
        String prompt = String.format(
                """
                        Eres un experto en precios para un sistema POS peruano.

                        Sugiere un precio realista en soles peruanos para:

                        Producto: %s
                        Categoría: %s
                        Descripción: %s

                        Considera estos rangos según el tipo de producto:
                        - Muebles grandes (mesas, camas, roperos): 400-1200 soles
                        - Muebles medianos (sillas, mesas pequeñas): 80-300 soles  
                        - Electrodomésticos: 200-800 soles
                        - Productos pequeños/consumibles: 5-50 soles
                        - Tecnología: 300-1500 soles

                        Responde SOLO con el número del precio (ej: 450.00), sin texto adicional ni símbolo de moneda.
                        """,
                productName,
                category != null ? category : "No especificada",
                description != null ? description : "Sin descripción");

        try {
            log.info("Generando precio para producto: {}", productName);
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            // Extraer solo el número del response
            String cleanResponse = response.trim().replaceAll("[^0-9.]", "");
            return cleanResponse;

        } catch (Exception e) {
            log.error("Error al generar precio con IA: {}", e.getMessage());
            return "50.00"; // Precio por defecto en caso de error
        }
    }

    @Override
    public String suggestDiscount(Product product, Double averageSales, Integer currentStock) {
        String prompt = String.format(
                """
                        Eres un analista de ventas experto en estrategias de descuentos.

                        Analiza la siguiente situación y sugiere un descuento estratégico:

                        Producto: %s
                        Precio actual: S/ %.2f
                        Promedio de ventas mensuales: %.1f unidades
                        Stock actual: %d unidades

                        Proporciona:
                        1. Porcentaje de descuento recomendado (entre 0%% y 30%%)
                        2. Justificación breve (máximo 80 palabras)
                        3. Objetivo esperado (ej: "Acelerar rotación", "Liquidar stock")

                        Formato de respuesta:
                        DESCUENTO: [X]%%
                        JUSTIFICACIÓN: [texto]
                        OBJETIVO: [texto]

                        Responde en español y sé conciso.
                        """,
                product.getName(),
                product.getPrice(),
                averageSales,
                currentStock);

        try {
            log.info("Sugiriendo descuento para producto ID: {}", product.getId());
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return response.trim();

        } catch (Exception e) {
            log.error("Error al sugerir descuento con IA: {}, generando sugerencia básica", e.getMessage());
            return generateBasicDiscountSuggestion(product, averageSales, currentStock);
        }
    }

    private String generateBasicDiscountSuggestion(Product product, Double averageSales, Integer currentStock) {
        StringBuilder suggestion = new StringBuilder();
        
        // Lógica básica para sugerir descuento
        int discountPercent = 0;
        String justification = "";
        String objective = "";
        
        if (currentStock > 50 && averageSales < 10) {
            discountPercent = 20;
            justification = "Stock elevado con ventas lentas requiere impulso para acelerar rotación.";
            objective = "Reducir inventario y generar flujo de caja";
        } else if (currentStock > 30 && averageSales < 20) {
            discountPercent = 15;
            justification = "Inventario moderadamente alto, descuento ayudará a mejorar rotación.";
            objective = "Optimizar rotación de inventario";
        } else if (currentStock < 10 && averageSales > 15) {
            discountPercent = 5;
            justification = "Producto de alta demanda con stock bajo, descuento mínimo para fidelizar clientes.";
            objective = "Mantener satisfacción del cliente";
        } else if (averageSales == 0) {
            discountPercent = 25;
            justification = "Producto sin movimiento requiere descuento significativo para generar interés.";
            objective = "Activar demanda del producto";
        } else {
            discountPercent = 10;
            justification = "Descuento estándar para mantener competitividad en el mercado.";
            objective = "Impulsar ventas generales";
        }
        
        BigDecimal discountedPrice = product.getPrice()
                .multiply(BigDecimal.valueOf(100 - discountPercent))
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        
        suggestion.append(String.format("DESCUENTO: %d%%\n", discountPercent));
        suggestion.append(String.format("PRECIO_ORIGINAL: %.2f\n", product.getPrice()));
        suggestion.append(String.format("PRECIO_CON_DESCUENTO: %.2f\n", discountedPrice));
        suggestion.append(String.format("JUSTIFICACION: %s\n", justification));
        suggestion.append(String.format("OBJETIVO: %s", objective));
        
        return suggestion.toString();
    }

    @Override
    public String analyzeSalesTrends(LocalDate startDate, LocalDate endDate, Double totalSales, String topProducts) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String prompt = String.format(
                """
                        Eres un analista de negocios especializado en tendencias de ventas.

                        Analiza las siguientes métricas de ventas y genera un reporte ejecutivo:

                        Periodo: %s al %s
                        Ventas totales: S/ %.2f
                        Productos más vendidos: %s

                        Genera un análisis (máximo 200 palabras) que incluya:
                        1. Resumen general del desempeño
                        2. Productos destacados y posibles razones
                        3. Recomendaciones estratégicas (inventario, promociones)
                        4. Perspectiva para el próximo periodo

                        Usa un tono profesional pero accesible.
                        Estructura el texto en párrafos cortos.
                        Responde en español.
                        """,
                startDate.format(formatter),
                endDate.format(formatter),
                totalSales,
                topProducts);

        try {
            log.info("Analizando tendencias de ventas del {} al {}", startDate, endDate);
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return response.trim();

        } catch (Exception e) {
            log.error("Error al analizar tendencias con IA: {}, generando análisis básico", e.getMessage());
            return generateBasicSalesAnalysis(startDate, endDate, totalSales, topProducts);
        }
    }

    private String generateBasicSalesAnalysis(LocalDate startDate, LocalDate endDate, Double totalSales, String topProducts) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        StringBuilder analysis = new StringBuilder();
        analysis.append(String.format("PERIODO: %s al %s\n", startDate.format(formatter), endDate.format(formatter)));
        analysis.append(String.format("VENTAS_TOTALES: %.2f\n", totalSales));
        
        if (topProducts != null && !topProducts.isEmpty()) {
            analysis.append(String.format("PRODUCTOS_DESTACADOS: %s\n", topProducts));
        }
        
        if (totalSales > 10000) {
            analysis.append("RENDIMIENTO: Excelente\n");
            analysis.append("EVALUACION: Las ventas superan expectativas del período\n");
            analysis.append("RECOMENDACION: Mantener estrategia actual y considerar expansión de inventario");
        } else if (totalSales > 5000) {
            analysis.append("RENDIMIENTO: Bueno\n");
            analysis.append("EVALUACION: Ventas en rango esperado para el período\n");
            analysis.append("RECOMENDACION: Impulsar promociones para productos de menor rotación");
        } else if (totalSales > 0) {
            analysis.append("RENDIMIENTO: Bajo\n");
            analysis.append("EVALUACION: Oportunidad de mejora significativa\n");
            analysis.append("RECOMENDACION: Revisar estrategia de precios y lanzar promociones especiales");
        } else {
            analysis.append("RENDIMIENTO: Sin ventas\n");
            analysis.append("EVALUACION: No se registraron ventas en el período\n");
            analysis.append("RECOMENDACION: Verificar estrategia de marketing y disponibilidad de productos");
        }
        
        return analysis.toString();
    }
}
