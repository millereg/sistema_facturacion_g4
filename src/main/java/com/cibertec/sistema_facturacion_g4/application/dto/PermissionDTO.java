package com.cibertec.sistema_facturacion_g4.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long moduleId;
    private String action;
    private Boolean active;
}
