package com.cibertec.sistema_facturacion_g4.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionsDTO {
    private String roleName;
    private String roleDescription;
    private List<Long> permissionIds;
    private List<PermissionDTO> permissions;
}
