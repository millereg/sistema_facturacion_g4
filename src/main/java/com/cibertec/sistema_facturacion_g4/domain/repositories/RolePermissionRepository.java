package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleName(String roleName);
    
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.roleName = :roleName")
    void deleteByRoleName(@Param("roleName") String roleName);
    
    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.roleName = :roleName AND rp.permissionId = :permissionId")
    void deleteByRoleNameAndPermissionId(@Param("roleName") String roleName, @Param("permissionId") Long permissionId);
}
