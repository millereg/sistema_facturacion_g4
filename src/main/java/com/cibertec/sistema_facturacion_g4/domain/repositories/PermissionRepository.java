package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByActiveTrue();
    
    Optional<Permission> findByCode(String code);
    
    List<Permission> findByModuleIdAndActiveTrue(Long moduleId);
    
    @Query("SELECT p FROM Permission p WHERE p.id IN " +
           "(SELECT rp.permissionId FROM RolePermission rp WHERE rp.roleName = :roleName)")
    List<Permission> findByRoleName(@Param("roleName") String roleName);
}
