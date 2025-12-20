package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.ModuleDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.PermissionDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.RolePermissionsDTO;

import java.util.List;

public interface PermissionService {
    List<ModuleDTO> getAllModules();
    List<ModuleDTO> getModulesHierarchy();
    ModuleDTO getModuleByCode(String code);
    
    List<PermissionDTO> getAllPermissions();
    List<PermissionDTO> getPermissionsByModule(Long moduleId);
    List<PermissionDTO> getPermissionsByRole(String roleName);
    PermissionDTO getPermissionByCode(String code);
    
    RolePermissionsDTO getRolePermissions(String roleName);
    void assignPermissionsToRole(String roleName, List<Long> permissionIds);
    void removePermissionFromRole(String roleName, Long permissionId);
    
    boolean hasPermission(String roleName, String permissionCode);
    List<String> getUserPermissions(String roleName);
}
