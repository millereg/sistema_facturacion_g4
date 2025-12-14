package com.cibertec.sistema_facturacion_g4.application.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
