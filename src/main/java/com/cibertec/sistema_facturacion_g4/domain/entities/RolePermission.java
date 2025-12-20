package com.cibertec.sistema_facturacion_g4.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
