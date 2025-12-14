package com.cibertec.sistema_facturacion_g4.domain.services;

import java.time.LocalDate;

import com.cibertec.sistema_facturacion_g4.domain.entities.Product;

public interface AIAssistantService {

    /**
     * Genera una descripción de producto profesional y atractiva
     * 
     * @param productName Nombre del producto
     * @param category    Categoría del producto
     * @return Descripción generada por IA
     */
    String generateProductDescription(String productName, String category);

    /**
     * Genera una sugerencia de precio realista para un producto
     * 
     * @param productName Nombre del producto
     * @param category    Categoría del producto
     * @param description Descripción adicional del producto
     * @return Precio sugerido por IA
     */
    String generateProductPrice(String productName, String category, String description);

    /**
     * Sugiere un descuento inteligente basado en historial de ventas
     * 
     * @param product      Producto a analizar
     * @param averageSales Promedio de ventas mensuales
     * @param currentStock Stock actual
     * @return Sugerencia de descuento con justificación
     */
    String suggestDiscount(Product product, Double averageSales, Integer currentStock);

    /**
     * Analiza tendencias de ventas y genera reporte en lenguaje natural
     * 
     * @param startDate   Fecha inicio del análisis
     * @param endDate     Fecha fin del análisis
     * @param totalSales  Total de ventas en el periodo
     * @param topProducts Productos más vendidos (separados por comas)
     * @return Análisis de tendencias en lenguaje natural
     */
    String analyzeSalesTrends(LocalDate startDate, LocalDate endDate, Double totalSales, String topProducts);
}
