package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.AssignPermissionsRequest;
import com.cibertec.sistema_facturacion_g4.application.dto.ModuleDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.PermissionDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.RolePermissionsDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permisos", description = "API para gestión de módulos y permisos")
public class PermissionController {
    private final PermissionService permissionService;

    @Operation(summary = "Obtener todos los módulos")
    @GetMapping("/modules")
    public ResponseEntity<List<ModuleDTO>> getAllModules() {
        return ResponseEntity.ok(permissionService.getAllModules());
    }

    @Operation(summary = "Obtener jerarquía de módulos")
    @GetMapping("/modules/hierarchy")
    public ResponseEntity<List<ModuleDTO>> getModulesHierarchy() {
        return ResponseEntity.ok(permissionService.getModulesHierarchy());
    }

    @Operation(summary = "Obtener todos los permisos")
    @GetMapping("/all")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @Operation(summary = "Obtener permisos por módulo")
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<PermissionDTO>> getPermissionsByModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(permissionService.getPermissionsByModule(moduleId));
    }

    @Operation(summary = "Obtener permisos de un rol")
    @GetMapping("/role/{roleName}")
    public ResponseEntity<RolePermissionsDTO> getRolePermissions(@PathVariable String roleName) {
        return ResponseEntity.ok(permissionService.getRolePermissions(roleName));
    }

    @Operation(summary = "Asignar permisos a un rol")
    @PostMapping("/assign")
    public ResponseEntity<String> assignPermissionsToRole(@RequestBody AssignPermissionsRequest request) {
        permissionService.assignPermissionsToRole(request.getRoleName(), request.getPermissionIds());
        return ResponseEntity.ok("Permisos asignados correctamente");
    }

    @Operation(summary = "Obtener permisos del usuario actual")
    @GetMapping("/user/permissions")
    public ResponseEntity<List<String>> getCurrentUserPermissions() {
        String role = com.cibertec.sistema_facturacion_g4.infrastructure.security.SecurityUtils.getCurrentUserRole();
        return ResponseEntity.ok(permissionService.getUserPermissions(role));
    }

    @Operation(summary = "Verificar permiso")
    @GetMapping("/check/{permissionCode}")
    public ResponseEntity<Boolean> checkPermission(@PathVariable String permissionCode) {
        String role = com.cibertec.sistema_facturacion_g4.infrastructure.security.SecurityUtils.getCurrentUserRole();
        return ResponseEntity.ok(permissionService.hasPermission(role, permissionCode));
    }
}
