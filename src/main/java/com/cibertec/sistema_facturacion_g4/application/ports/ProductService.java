package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductDTO save(ProductDTO product);

    Optional<ProductDTO> findById(Long id);

    List<ProductDTO> findAll();

    void deleteById(Long id);
}
