package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.inventory.*;
import java.util.List;

public interface InventoryService {
    void adjustStock(StockAdjustmentRequest request, Long userId);

    void registerStockIn(Long productId, Integer quantity, String reason, String notes, Long userId);

    void registerStockOut(Long productId, Integer quantity, String reason, String notes, Long userId);

    List<StockReportDTO> getLowStockReport();

    StockReportDTO getProductStockReport(Long productId);

    List<StockMovementDTO> getProductMovements(Long productId);

    List<StockMovementDTO> getAllStockMovements();
}