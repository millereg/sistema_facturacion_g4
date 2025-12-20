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
                        Eres un experto en análisis de precios de mercado peruano.

                        IMPORTANTE: Debes analizar cuidadosamente y dar un precio VARIABLE basado en el producto específico.

                        Producto: %s
                        Categoría: %s
                        Descripción: %s

                        RANGOS DE REFERENCIA (analiza el producto y elige dentro del rango apropiado):
                        - Sofás, salas, camas grandes: S/ 800-2500
                        - Mesas de comedor, escritorios: S/ 300-900
                        - Sillas, taburetes: S/ 60-250
                        - Electrodomésticos grandes (refrigeradoras, lavadoras): S/ 800-2500
                        - Electrodomésticos medianos (microondas, licuadoras): S/ 150-500
                        - Artículos pequeños, accesorios: S/ 15-80
                        - Laptops, computadoras: S/ 1200-4500
                        - Celulares, tablets: S/ 400-2500
                        - Herramientas: S/ 50-400
                        - Decoración, cuadros: S/ 30-200

                        INSTRUCCIONES:
                        1. Lee el nombre del producto y su descripción
                        2. Identifica el tipo de producto específico
                        3. Elige un precio REALISTA dentro del rango
                        4. Considera calidad mencionada en la descripción
                        5. NUNCA des el mismo precio para productos diferentes

                        Responde SOLO con el número decimal (ejemplo: 459.00 o 1250.00 o 85.50)
                        NO incluyas texto, símbolos de moneda ni explicaciones.
                        """,
                productName,
                category != null ? category : "No especificada",
                description != null && !description.isEmpty() ? description : "Sin descripción");

        try {
            log.info("Generando precio para producto: {}", productName);
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            String cleanResponse = response.trim()
                    .replaceAll("[^0-9.]", "")
                    .replaceAll("\\.{2,}", ".");
            
            if (cleanResponse.isEmpty() || cleanResponse.equals(".")) {
                log.warn("Respuesta inválida de IA: '{}', usando precio por defecto", response);
                return "50.00";
            }
            
            try {
                double price = Double.parseDouble(cleanResponse);
                if (price < 1.0) price = 50.00;
                if (price > 10000.0) price = 1000.00;
                return String.format("%.2f", price);
            } catch (NumberFormatException nfe) {
                log.error("No se pudo parsear precio: {}", cleanResponse);
                return "50.00";
            }

        } catch (Exception e) {
            log.error("Error al generar precio con IA: {}", e.getMessage());
            return "50.00";
        }
    }

    @Override
    public String suggestDiscount(Product product, Double averageSales, Integer currentStock) {
        String prompt = String.format(
                """
                        Eres un analista de ventas experto en estrategias de descuentos.

                        IMPORTANTE: Debes sugerir SIEMPRE un descuento real, nunca 0%%.

                        Analiza la siguiente situación y sugiere un descuento estratégico:

                        Producto: %s
                        Precio actual: S/ %.2f
                        Promedio de ventas mensuales: %.1f unidades
                        Stock actual: %d unidades

                        REGLAS OBLIGATORIAS:
                        - El descuento DEBE estar entre 5%% y 30%% (NUNCA 0%%)
                        - Stock alto (>50) o ventas bajas (<10): descuento 20-30%%
                        - Stock medio (20-50): descuento 10-20%%
                        - Stock bajo (<20) o ventas altas (>20): descuento 5-10%%
                        - Sin ventas (0): descuento 25-30%%

                        Formato de respuesta EXACTO:
                        DESCUENTO: [número]%%
                        JUSTIFICACIÓN: [texto breve]
                        OBJETIVO: [texto breve]

                        Ejemplo:
                        DESCUENTO: 15%%
                        JUSTIFICACIÓN: Stock moderado requiere impulso para mejorar rotación
                        OBJETIVO: Aumentar ventas en 30%%

                        Responde SOLO con el formato indicado, nada más.
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
