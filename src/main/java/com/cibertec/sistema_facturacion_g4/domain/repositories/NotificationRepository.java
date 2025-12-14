package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCompanyIdAndIsReadOrderByCreatedAtDesc(Long companyId, Boolean isRead);

    List<Notification> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Long countByCompanyIdAndIsRead(Long companyId, Boolean isRead);

    Long countByUserIdAndIsRead(Long userId, Boolean isRead);

    List<Notification> findByCompanyIdAndCreatedAtBetween(Long companyId, LocalDateTime start, LocalDateTime end);
}
