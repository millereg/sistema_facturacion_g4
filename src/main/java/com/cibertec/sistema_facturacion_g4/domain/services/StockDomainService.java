package com.cibertec.sistema_facturacion_g4.domain.services;

import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import java.util.List;

public interface StockDomainService {
    boolean isStockLow(Product product);

    int getSuggestedReorder(Product product);

    void registerStockMovement(Product product, int quantity, String type);

    List<Product> getProductsWithLowStock(List<Product> products);
}
