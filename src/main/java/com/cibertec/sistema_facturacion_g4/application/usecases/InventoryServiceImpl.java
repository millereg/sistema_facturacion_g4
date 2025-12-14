package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.ports.InventoryService;
import com.cibertec.sistema_facturacion_g4.application.dto.inventory.*;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.entities.Category;
import com.cibertec.sistema_facturacion_g4.domain.entities.Supplier;
import com.cibertec.sistema_facturacion_g4.domain.entities.StockMovement;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ProductRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CategoryRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.SupplierRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.StockMovementRepository;
import com.cibertec.sistema_facturacion_g4.domain.services.StockDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockDomainService stockDomainService;

    @Override
    public void adjustStock(StockAdjustmentRequest request, Long userId) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Integer oldStock = product.getStock() != null ? product.getStock() : 0;
        Integer newStock = request.getNewStock();
        
        product.setStock(newStock);
        productRepository.save(product);
        
        // Guardar movimiento en BD
        StockMovement movement = new StockMovement();
        movement.setProductId(product.getId());
        movement.setQuantity(Math.abs(newStock - oldStock));
        movement.setMovementType("ADJUSTMENT");
        movement.setReason(request.getReason());
        movement.setNotes(request.getNotes());
        movement.setUserId(userId);
        movement.setMovementDate(LocalDateTime.now());
        
        stockMovementRepository.save(movement);
    }

    @Override
    public void registerStockIn(Long productId, Integer quantity, String reason, String notes, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        stockDomainService.registerStockMovement(product, quantity, "IN");
        productRepository.save(product);
        
        // Guardar movimiento en BD
        StockMovement movement = new StockMovement();
        movement.setProductId(product.getId());
        movement.setQuantity(quantity);
        movement.setMovementType("IN");
        movement.setReason(reason);
        movement.setNotes(notes);
        movement.setUserId(userId);
        movement.setMovementDate(LocalDateTime.now());
        
        stockMovementRepository.save(movement);
    }

    @Override
    public void registerStockOut(Long productId, Integer quantity, String reason, String notes, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente. Stock actual: " + product.getStock());
        }
        stockDomainService.registerStockMovement(product, quantity, "OUT");
        productRepository.save(product);
        
        StockMovement movement = new StockMovement();
        movement.setProductId(product.getId());
        movement.setQuantity(quantity);
        movement.setMovementType("OUT");
        movement.setReason(reason);
        movement.setNotes(notes);
        movement.setUserId(userId);
        movement.setMovementDate(LocalDateTime.now());
        
        stockMovementRepository.save(movement);
    }

    @Override
    public List<StockReportDTO> getLowStockReport() {
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .map(product -> {
                    StockReportDTO report = new StockReportDTO();
                    report.setProductId(product.getId());
                    report.setProductCode(product.getCode());
                    report.setProductName(product.getName());
                    report.setCurrentStock(product.getStock() != null ? product.getStock() : 0);
                    report.setMinimumStock(5);
                    report.setIsLowStock(stockDomainService.isStockLow(product));
                    report.setSuggestedReorder(stockDomainService.getSuggestedReorder(product));
                    
                    String categoryName = categoryRepository.findById(product.getCategoryId())
                            .map(Category::getName)
                            .orElse("Sin categoría");
                    report.setCategoryName(categoryName);
                    
                    String supplierName = supplierRepository.findById(product.getSupplierId())
                            .map(Supplier::getName)
                            .orElse("Sin proveedor");
                    report.setSupplierName(supplierName);
                    
                    return report;
                })
                .collect(Collectors.toList());
    }

    @Override
    public StockReportDTO getProductStockReport(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        StockReportDTO report = new StockReportDTO();
        report.setProductId(product.getId());
        report.setProductCode(product.getCode());
        report.setProductName(product.getName());
        report.setCurrentStock(product.getStock() != null ? product.getStock() : 0);
        report.setMinimumStock(5);
        report.setIsLowStock(stockDomainService.isStockLow(product));
        report.setSuggestedReorder(stockDomainService.getSuggestedReorder(product));
        
        String categoryName = categoryRepository.findById(product.getCategoryId())
                .map(Category::getName)
                .orElse("Sin categoría");
        report.setCategoryName(categoryName);
        
        String supplierName = supplierRepository.findById(product.getSupplierId())
                .map(Supplier::getName)
                .orElse("Sin proveedor");
        report.setSupplierName(supplierName);
        
        return report;
    }

    @Override
    public List<StockMovementDTO> getProductMovements(Long productId) {
        List<StockMovement> movements = stockMovementRepository.findByProductIdOrderByMovementDateDesc(productId);
        return movements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementDTO> getAllStockMovements() {
        List<StockMovement> movements = stockMovementRepository.findAllByOrderByMovementDateDesc();
        return movements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private StockMovementDTO convertToDTO(StockMovement movement) {
        Product product = productRepository.findById(movement.getProductId()).orElse(null);
        
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        dto.setProductId(movement.getProductId());
        dto.setProductCode(product != null ? product.getCode() : "N/A");
        dto.setProductName(product != null ? product.getName() : "Producto no encontrado");
        dto.setQuantity(movement.getQuantity());
        dto.setMovementType(movement.getMovementType());
        dto.setReason(movement.getReason());
        dto.setNotes(movement.getNotes());
        dto.setMovementDate(movement.getMovementDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setUserId(movement.getUserId());
        dto.setUserName("Usuario " + movement.getUserId());
        
        return dto;
    }
}