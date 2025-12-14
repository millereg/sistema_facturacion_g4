package com.cibertec.sistema_facturacion_g4.application.dto;

import com.cibertec.sistema_facturacion_g4.domain.entities.Customer.CustomerType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    private Long id;
    private CustomerType type;
    private String documentType;
    private String documentNumber;
    private String businessName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private Long companyId;
    private Boolean isGeneric;
    private Boolean active;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
