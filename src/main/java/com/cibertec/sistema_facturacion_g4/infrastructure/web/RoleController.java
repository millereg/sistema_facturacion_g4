package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.ports.RoleService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllActiveRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Map<String, String> request) {
        Role role = Role.builder()
                .name(request.get("name"))
                .description(request.get("description"))
                .active(true)
                .build();

        Role saved = roleService.createRole(role);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/init-defaults")
    public ResponseEntity<Map<String, Object>> initializeDefaultRoles() {
        Map<String, Object> result = roleService.initializeDefaultRoles();
        return ResponseEntity.ok(result);
    }
}