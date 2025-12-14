package com.cibertec.sistema_facturacion_g4.application.dto;

import lombok.Data;


@Data
public class SystemConfigurationDTO {
    private Long id;
    private String key;
    private String value;
    private String description;
    private Long companyId;
    private Boolean active;
    private String createdAt;
    private String updatedAt;
}