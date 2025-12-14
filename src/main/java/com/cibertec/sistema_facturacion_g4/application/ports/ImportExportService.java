package com.cibertec.sistema_facturacion_g4.application.ports;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ImportExportService {
    Map<String, Object> importProducts(MultipartFile file);

    Map<String, Object> importCustomers(MultipartFile file);

    Map<String, Object> exportProducts();

    Map<String, Object> exportCustomers();

    Map<String, Object> generateProductTemplate();

    Map<String, Object> generateCustomerTemplate();
}