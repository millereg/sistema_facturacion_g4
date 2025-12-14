package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.pos.*;
import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.POSService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class POSController {
    private final POSService posService;

    @PostMapping("/sales")
    public ResponseEntity<SaleResponse> processSale(@RequestBody SaleRequest saleRequest,
            @RequestParam(defaultValue = "1") Long userId) {
        SaleResponse response = posService.processSale(saleRequest, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam(required = false) String searchTerm) {
        List<ProductDTO> products = posService.searchProducts(searchTerm);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getProductsForSale() {
        List<ProductDTO> products = posService.searchProducts(null);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/code/{code}")
    public ResponseEntity<ProductDTO> findProductByCode(@PathVariable String code) {
        ProductDTO product = posService.findProductByCode(code);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customers/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(
            @RequestParam(required = false) String searchTerm) {
        List<CustomerDTO> customers = posService.searchCustomers(searchTerm);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        CustomerDTO customer = posService.getCustomerById(id);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("POS System is running");
    }
}