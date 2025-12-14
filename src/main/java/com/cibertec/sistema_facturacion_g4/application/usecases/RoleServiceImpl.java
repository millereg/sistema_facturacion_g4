package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.ports.RoleService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Role;
import com.cibertec.sistema_facturacion_g4.domain.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    
    @Override
    public List<Role> getAllActiveRoles() {
        return roleRepository.findByActiveTrue();
    }
    
    @Override
    public Role createRole(Role inputRole) {
        Role role = Role.builder()
                .name(inputRole.getName())
                .description(inputRole.getDescription())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        return roleRepository.save(role);
    }
    
    @Override
    public Map<String, Object> initializeDefaultRoles() {
        String[] defaultRoles = {"ADMIN", "VENDEDOR", "CAJERO", "GERENTE"};
        String[] descriptions = {
            "Administrador del sistema",
            "Vendedor con acceso a ventas",
            "Cajero con acceso limitado",
            "Gerente con acceso a reportes"
        };
        
        int created = 0;
        for (int i = 0; i < defaultRoles.length; i++) {
            if (!roleRepository.findByName(defaultRoles[i]).isPresent()) {
                Role role = Role.builder()
                        .name(defaultRoles[i])
                        .description(descriptions[i])
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .build();
                        
                roleRepository.save(role);
                created++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Roles inicializados");
        result.put("created", created);
        
        return result;
    }
}
