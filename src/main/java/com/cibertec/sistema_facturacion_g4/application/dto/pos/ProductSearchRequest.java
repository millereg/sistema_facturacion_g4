package com.cibertec.sistema_facturacion_g4.application.dto.pos;

import lombok.Data;

@Data
public class ProductSearchRequest {
    private String searchTerm;
    private Boolean onlyWithStock = true;
}