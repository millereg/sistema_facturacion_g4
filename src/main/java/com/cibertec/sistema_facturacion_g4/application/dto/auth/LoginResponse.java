package com.cibertec.sistema_facturacion_g4.application.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private Long userId;
    private String username;
    private String role;
    private Long companyId;
    private String companyName;
    private String firstName;
    private String lastName;
    private List<String> permissions;
}
