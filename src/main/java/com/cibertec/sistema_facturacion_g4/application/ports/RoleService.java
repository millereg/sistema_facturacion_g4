package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.domain.entities.Role;

import java.util.List;
import java.util.Map;

public interface RoleService {
    List<Role> getAllActiveRoles();

    Role createRole(Role role);

    Map<String, Object> initializeDefaultRoles();

    void activateRole(Long id);

    void deactivateRole(Long id);

    void deleteRole(Long id);
}
