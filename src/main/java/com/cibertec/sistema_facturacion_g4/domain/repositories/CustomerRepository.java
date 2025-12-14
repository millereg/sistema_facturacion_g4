package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByCompanyId(Long companyId);

    List<Customer> findByCompanyIdAndActiveTrue(Long companyId);

    Optional<Customer> findByIdAndCompanyId(Long id, Long companyId);

    Optional<Customer> findByDocumentNumberAndCompanyId(String documentNumber, Long companyId);

    Optional<Customer> findByCompanyIdAndIsGenericTrue(Long companyId);

    @Query("SELECT c FROM Customer c WHERE c.companyId = :companyId AND " +
            "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.businessName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "c.documentNumber LIKE CONCAT('%', :searchTerm, '%')) " +
            "AND c.active = true")
    List<Customer> searchByCompanyIdAndTerm(
            @Param("companyId") Long companyId,
            @Param("searchTerm") String searchTerm);
}