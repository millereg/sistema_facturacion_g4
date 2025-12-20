package com.cibertec.sistema_facturacion_g4.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private Integer stock;
    private String imageUrl;
    private Long categoryId;
    private Long supplierId;
    private Long companyId;
    private Boolean active;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    public Boolean getHasStock() {
        return stock != null && stock > 0;
    }
}
