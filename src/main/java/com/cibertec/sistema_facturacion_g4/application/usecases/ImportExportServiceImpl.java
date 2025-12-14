package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.ports.ImportExportService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.entities.Customer;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ProductRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CustomerRepository;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImportExportServiceImpl implements ImportExportService {
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Map<String, Object> importProducts(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        Long companyId = TenantUtils.getCurrentCompanyId();

        try {
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "Archivo vacío");
                return result;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            int processed = 0;
            int errors = 0;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length >= 4) {
                        Product product = Product.builder()
                                .code(fields[0].trim())
                                .name(fields[1].trim())
                                .description(fields.length > 2 ? fields[2].trim() : "")
                                .price(new BigDecimal(fields[3].trim()))
                                .stock(fields.length > 4 ? Integer.parseInt(fields[4].trim()) : 0)
                                .companyId(companyId)
                                .active(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                        productRepository.save(product);
                        processed++;
                    }
                } catch (Exception e) {
                    errors++;
                }
            }

            result.put("success", true);
            result.put("message", "Productos importados correctamente");
            result.put("imported", processed);
            result.put("failed", errors);
            result.put("filename", file.getOriginalFilename());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error al procesar archivo: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> importCustomers(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        Long companyId = TenantUtils.getCurrentCompanyId();

        try {
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "Archivo vacío");
                return result;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            int processed = 0;
            int errors = 0;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    String[] fields = line.split(",");
                    if (fields.length >= 3) {
                        String documento = fields[0].trim();

                        boolean esEmpresa = documento.length() == 11 && documento.startsWith("20");
                        Customer.CustomerType tipo = esEmpresa ? Customer.CustomerType.COMPANY
                                : Customer.CustomerType.PERSON;
                        String tipoDoc = esEmpresa ? "RUC" : "DNI";

                        Customer.CustomerBuilder builder = Customer.builder()
                                .documentNumber(documento)
                                .phone(fields.length > 3 ? fields[3].trim() : "")
                                .address(fields.length > 4 ? fields[4].trim() : "")
                                .companyId(companyId)
                                .active(true)
                                .type(tipo)
                                .documentType(tipoDoc)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now());

                        if (esEmpresa) {
                            builder.businessName(fields[1].trim());
                        } else {
                            builder.firstName(fields[1].trim())
                                    .lastName(fields.length > 2 ? fields[2].trim() : "");
                        }

                        Customer customer = builder.build();

                        customerRepository.save(customer);
                        processed++;
                    }
                } catch (Exception e) {
                    errors++;
                }
            }

            result.put("success", true);
            result.put("message", "Clientes importados correctamente");
            result.put("imported", processed);
            result.put("failed", errors);
            result.put("filename", file.getOriginalFilename());

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error al procesar archivo: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> exportProducts() {
        Map<String, Object> result = new HashMap<>();
        Long companyId = TenantUtils.getCurrentCompanyId();

        try {
            List<Product> products = productRepository.findByCompanyId(companyId);

            result.put("success", true);
            result.put("message", "Exportación completada");
            result.put("recordCount", products.size());
            result.put("downloadUrl", "/api/downloads/productos_" + System.currentTimeMillis() + ".csv");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error al exportar: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> exportCustomers() {
        Map<String, Object> result = new HashMap<>();
        Long companyId = TenantUtils.getCurrentCompanyId();

        try {
            List<Customer> customers = customerRepository.findByCompanyId(companyId);

            result.put("success", true);
            result.put("message", "Exportación completada");
            result.put("recordCount", customers.size());
            result.put("downloadUrl", "/api/downloads/clientes_" + System.currentTimeMillis() + ".csv");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error al exportar: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> generateProductTemplate() {
        Map<String, Object> result = new HashMap<>();

        result.put("success", true);
        result.put("message", "Plantilla generada");
        result.put("downloadUrl", "/api/downloads/plantilla_productos.csv");
        result.put("columns", new String[] { "codigo", "nombre", "descripcion", "precio", "stock" });
        result.put("example", "PROD001,Laptop HP,Laptop HP Pavilion,1500.00,10");

        return result;
    }

    @Override
    public Map<String, Object> generateCustomerTemplate() {
        Map<String, Object> result = new HashMap<>();

        result.put("success", true);
        result.put("message", "Plantilla generada");
        result.put("downloadUrl", "/api/downloads/plantilla_clientes.csv");
        result.put("columns",
                new String[] { "documento", "nombre_o_razon_social", "apellido", "telefono", "direccion" });
        result.put("example", "12345678,Juan,Perez,987654321,Av. Lima 123");
        result.put("exampleCompany", "20123456789,MI EMPRESA SAC,,987654321,Jr. Comercio 456");
        result.put("instructions",
                "DNI: 8 dígitos | RUC: 11 dígitos (inicia con 20). Si es empresa, dejar apellido vacío.");

        return result;
    }
}