package com.cibertec.sistema_facturacion_g4.domain.services.impl;

import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.services.StockDomainService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockDomainServiceImpl implements StockDomainService {
    private static final int MIN_STOCK = 5;
    private static final int SUGGESTED_REORDER = 20;

    @Override
    public boolean isStockLow(Product product) {
        return product.getStock() != null && product.getStock() < MIN_STOCK;
    }

    @Override
    public int getSuggestedReorder(Product product) {
        return SUGGESTED_REORDER - (product.getStock() != null ? product.getStock() : 0);
    }

    @Override
    public void registerStockMovement(Product product, int quantity, String type) {
        if ("IN".equalsIgnoreCase(type)) {
            product.setStock((product.getStock() != null ? product.getStock() : 0) + quantity);
        } else if ("OUT".equalsIgnoreCase(type)) {
            product.setStock((product.getStock() != null ? product.getStock() : 0) - quantity);
        }
    }

    @Override
    public List<Product> getProductsWithLowStock(List<Product> products) {
        return products.stream().filter(this::isStockLow).collect(Collectors.toList());
    }
}
