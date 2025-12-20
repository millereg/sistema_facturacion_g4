package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByActiveTrue();
    
    Optional<Module> findByCode(String code);
    
    List<Module> findByParentIdIsNullAndActiveTrue();
    
    List<Module> findByParentIdAndActiveTrue(Long parentId);
}
