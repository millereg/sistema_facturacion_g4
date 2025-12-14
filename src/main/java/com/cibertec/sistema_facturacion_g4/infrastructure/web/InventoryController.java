package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.inventory.*;
import com.cibertec.sistema_facturacion_g4.application.ports.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PutMapping("/adjust")
    public ResponseEntity<String> adjustStock(@RequestBody StockAdjustmentRequest request,
            @RequestParam(defaultValue = "1") Long userId) {
        inventoryService.adjustStock(request, userId);
        return ResponseEntity.ok("Stock ajustado correctamente");
    }

    @PostMapping("/stock-in")
    public ResponseEntity<String> registerStockIn(@RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "1") Long userId) {
        inventoryService.registerStockIn(productId, quantity, reason, notes, userId);
        return ResponseEntity.ok("Entrada de stock registrada correctamente");
    }

    @PostMapping("/stock-out")
    public ResponseEntity<String> registerStockOut(@RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String reason,
            @RequestParam(required = false) String notes,
            @RequestParam(defaultValue = "1") Long userId) {
        inventoryService.registerStockOut(productId, quantity, reason, notes, userId);
        return ResponseEntity.ok("Salida de stock registrada correctamente");
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<StockReportDTO>> getLowStockReport() {
        List<StockReportDTO> report = inventoryService.getLowStockReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/products/{productId}/stock")
    public ResponseEntity<StockReportDTO> getProductStockReport(@PathVariable Long productId) {
        StockReportDTO report = inventoryService.getProductStockReport(productId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/products/{productId}/movements")
    public ResponseEntity<List<StockMovementDTO>> getProductMovements(@PathVariable Long productId) {
        List<StockMovementDTO> movements = inventoryService.getProductMovements(productId);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/movements")
    public ResponseEntity<List<StockMovementDTO>> getAllStockMovements() {
        List<StockMovementDTO> movements = inventoryService.getAllStockMovements();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Inventory module is running");
    }
}