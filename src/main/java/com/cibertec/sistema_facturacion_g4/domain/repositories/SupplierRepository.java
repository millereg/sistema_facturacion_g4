package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
       List<Supplier> findByCompanyId(Long companyId);

       Optional<Supplier> findByIdAndCompanyId(Long id, Long companyId);

       List<Supplier> findByCompanyIdAndActiveTrue(Long companyId);

       @Query("SELECT s FROM Supplier s WHERE s.companyId = :companyId " +
                     "AND (s.name LIKE %:searchTerm% " +
                     "OR s.documentNumber LIKE %:searchTerm% " +
                     "OR s.phone LIKE %:searchTerm%) " +
                     "ORDER BY s.name ASC")
       List<Supplier> searchByCompanyIdAndTerm(@Param("companyId") Long companyId,
                     @Param("searchTerm") String searchTerm);
}