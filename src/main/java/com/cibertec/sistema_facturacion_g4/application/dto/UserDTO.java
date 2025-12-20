package com.cibertec.sistema_facturacion_g4.application.dto;

import com.cibertec.sistema_facturacion_g4.shared.constants.UserRoles;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserRoles role;
    private Long companyId;
    private Boolean active;
    private List<String> permissions;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
