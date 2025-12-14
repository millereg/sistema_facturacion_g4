package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.CustomerService;
import com.cibertec.sistema_facturacion_g4.application.ports.ImportExportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final ImportExportService importExportService;

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAll() {
        List<CustomerDTO> lista = customerService.findAll();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/generic")
    public ResponseEntity<CustomerDTO> getGenericCustomer() {
        var genericCustomerOpt = customerService.getGenericCustomer();
        if (genericCustomerOpt.isPresent()) {
            return ResponseEntity.ok(genericCustomerOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {
        var clienteOpt = customerService.findById(id);
        if (clienteOpt.isPresent()) {
            return ResponseEntity.ok(clienteOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerDTO dto) {
        CustomerDTO saved = customerService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, @Valid @RequestBody CustomerDTO dto) {
        dto.setId(id);
        CustomerDTO updated = customerService.save(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importCustomers(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = importExportService.importCustomers(file);
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCustomers() {
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("documento,nombres,apellidos,razon_social,telefono,email,direccion,activo\n");
            
            List<CustomerDTO> customers = customerService.findAll();
            
            for (CustomerDTO customer : customers) {
                csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    escapeCSV(customer.getDocumentNumber() != null ? customer.getDocumentNumber() : ""),
                    escapeCSV(customer.getFirstName() != null ? customer.getFirstName() : ""),
                    escapeCSV(customer.getLastName() != null ? customer.getLastName() : ""),
                    escapeCSV(customer.getBusinessName() != null ? customer.getBusinessName() : ""),
                    escapeCSV(customer.getPhone() != null ? customer.getPhone() : ""),
                    escapeCSV(customer.getEmail() != null ? customer.getEmail() : ""),
                    escapeCSV(customer.getAddress() != null ? customer.getAddress() : ""),
                    customer.getActive() != null && customer.getActive() ? "SI" : "NO"
                ));
            }
            
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] result = new byte[bom.length + csvBytes.length];
            System.arraycopy(bom, 0, result, 0, bom.length);
            System.arraycopy(csvBytes, 0, result, bom.length, csvBytes.length);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"clientes_" + System.currentTimeMillis() + ".csv\"")
                    .body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> getCustomerTemplate() {
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("documento,nombres,apellidos,razon_social,telefono,email,direccion\n");
            csv.append("12345678,Juan,Perez,,987654321,juan@email.com,Av. Lima 123\n");
            csv.append("20123456789,,,MI EMPRESA SAC,987654321,empresa@email.com,Jr. Comercio 456\n");
            
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] result = new byte[bom.length + csvBytes.length];
            System.arraycopy(bom, 0, result, 0, bom.length);
            System.arraycopy(csvBytes, 0, result, bom.length, csvBytes.length);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"plantilla_clientes.csv\"")
                    .body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
