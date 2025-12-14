package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId);
    
    List<StockMovement> findAllByOrderByMovementDateDesc();
}
