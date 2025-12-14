package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.pos.SaleRequest;
import com.cibertec.sistema_facturacion_g4.application.ports.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<InvoiceDTO> registerSale(@RequestBody SaleRequest request) {
        InvoiceDTO sale = saleService.registerSale(
                request.getInvoice(),
                request.getCustomer(),
                request.getProducts(),
                request.getPayments());
        return ResponseEntity.ok(sale);
    }

    @PutMapping("/{saleId}/cancel")
    public ResponseEntity<String> cancelSale(@PathVariable Long saleId,
            @RequestParam String reason) {
        boolean cancelled = saleService.cancelSale(saleId, reason);
        if (cancelled) {
            return ResponseEntity.ok("Venta anulada correctamente");
        } else {
            return ResponseEntity.badRequest().body("No se pudo anular la venta");
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InvoiceDTO>> getSalesByCustomer(@PathVariable Long customerId) {
        List<InvoiceDTO> sales = saleService.getSalesByCustomer(customerId);
        return ResponseEntity.ok(sales);
    }

    @GetMapping("/period")
    public ResponseEntity<List<InvoiceDTO>> getSalesByPeriod(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<InvoiceDTO> sales = saleService.getSalesByPeriod(startDate, endDate);
        return ResponseEntity.ok(sales);
    }
}