package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCompanyId(Long companyId);

    Optional<Payment> findByIdAndCompanyId(Long id, Long companyId);

    List<Payment> findByInvoiceIdAndCompanyId(Long invoiceId, Long companyId);

    @Query("SELECT p FROM Payment p WHERE p.companyId = :companyId AND p.invoiceId IN (SELECT i.id FROM Invoice i WHERE i.companyId = :companyId AND i.userId = :userId)")
    List<Payment> findByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);

    List<Payment> findByCompanyIdAndPaymentDateBetween(
            Long companyId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.companyId = :companyId AND p.paymentDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal getTotalPaymentsByCompanyAndDateRange(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
