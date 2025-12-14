package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCompanyId(Long companyId);

    Optional<Category> findByIdAndCompanyId(Long id, Long companyId);

    List<Category> findByCompanyIdAndActiveTrue(Long companyId);

    Optional<Category> findByNameAndCompanyId(String name, Long companyId);
}
