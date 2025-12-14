package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.CategoryDTO;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryDTO save(CategoryDTO category);

    Optional<CategoryDTO> findById(Long id);

    List<CategoryDTO> findAll();

    List<CategoryDTO> findAllActive();

    void deleteById(Long id);

    void activateCategory(Long id);

    void deactivateCategory(Long id);
}