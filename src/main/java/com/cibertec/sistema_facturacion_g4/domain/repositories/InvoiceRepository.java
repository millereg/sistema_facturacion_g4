package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
       List<Invoice> findByCustomerId(Long customerId);

       List<Invoice> findByUserId(Long userId);

       List<Invoice> findByStatus(Invoice.InvoiceStatus status);

       List<Invoice> findByCompanyId(Long companyId);

       List<Invoice> findByCompanyIdAndUserId(Long companyId, Long userId);

       Optional<Invoice> findByIdAndCompanyId(Long id, Long companyId);

       List<Invoice> findByCompanyIdAndStatus(Long companyId, Invoice.InvoiceStatus status);

       @Query("SELECT i FROM Invoice i WHERE i.companyId = :companyId " +
                     "AND i.issueDate BETWEEN :startDate AND :endDate " +
                     "ORDER BY i.issueDate DESC")
       List<Invoice> findByCompanyIdAndDateRange(@Param("companyId") Long companyId,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       @Query("SELECT i FROM Invoice i WHERE i.companyId = :companyId " +
                     "AND i.userId = :userId " +
                     "AND i.issueDate BETWEEN :startDate AND :endDate " +
                     "ORDER BY i.issueDate DESC")
       List<Invoice> findByCompanyIdAndUserIdAndDateRange(@Param("companyId") Long companyId,
                     @Param("userId") Long userId,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       @Query("SELECT i FROM Invoice i WHERE i.companyId = :companyId " +
                     "AND i.customerId = :customerId ORDER BY i.issueDate DESC")
       List<Invoice> findByCompanyIdAndCustomerId(@Param("companyId") Long companyId,
                     @Param("customerId") Long customerId);

       @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(i.number, LOCATE('-', i.number) + 1, LENGTH(i.number)) AS long)), 0) "
                     +
                     "FROM Invoice i WHERE i.companyId = :companyId AND i.series = :series")
       Long findLastCorrelativeBySeries(@Param("series") String series, @Param("companyId") Long companyId);

       List<Invoice> findByIssueDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}