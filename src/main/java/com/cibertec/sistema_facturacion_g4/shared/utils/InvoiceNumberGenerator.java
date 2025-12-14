package com.cibertec.sistema_facturacion_g4.shared.utils;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class InvoiceNumberGenerator {
    private static final String DEFAULT_SERIES = "F001";
    
    public String generateInvoiceNumber(String series, Long lastCorrelative) {
        String serieToUse = series != null && !series.isEmpty() ? series : DEFAULT_SERIES;
        Long correlative = lastCorrelative != null ? lastCorrelative + 1 : 1L;
        return String.format("%s-%08d", serieToUse, correlative);
    }
    
    public String generateBolletaNumber(String series, Long lastCorrelative) {
        String serieToUse = series != null && !series.isEmpty() ? series : "B001";
        Long correlative = lastCorrelative != null ? lastCorrelative + 1 : 1L;
        return String.format("%s-%08d", serieToUse, correlative);
    }
    
    public String generateTempNumber() {
        return "TEMP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
