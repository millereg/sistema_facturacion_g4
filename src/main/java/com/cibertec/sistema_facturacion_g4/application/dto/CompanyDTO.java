package com.cibertec.sistema_facturacion_g4.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyDTO {
    private Long id;
    private String businessName;
    private String tradeName;
    private String taxId;
    private String address;
    private String district;
    private String province;
    private String department;
    private String country;
    private String phone;
    private String email;
    private String website;
    private String logoUrl;
    private String economicActivity;
    private Boolean active;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
