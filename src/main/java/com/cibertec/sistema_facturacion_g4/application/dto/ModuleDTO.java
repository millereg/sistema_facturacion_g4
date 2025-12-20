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
public class ModuleDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private String route;
    private Long parentId;
    private Integer displayOrder;
    private Boolean active;
    private List<PermissionDTO> permissions;
    private List<ModuleDTO> children;
}
