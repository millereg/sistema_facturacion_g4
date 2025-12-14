package com.cibertec.sistema_facturacion_g4.application.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para generar descripción de producto con IA")
public class ProductDescriptionRequest {
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Schema(description = "Nombre del producto", example = "Sofá Moderno 3 Plazas")
    private String productName;
    
    @NotBlank(message = "La categoría es obligatoria")
    @Schema(description = "Categoría del producto", example = "Muebles de Sala")
    private String category;
}
