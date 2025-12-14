package com.cibertec.sistema_facturacion_g4.application.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para generar precio de producto con IA")
public class ProductPriceRequest {
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Schema(description = "Nombre del producto", example = "Mesa Comedor 6 Personas")
    private String productName;
    
    @Schema(description = "Categoría del producto", example = "Muebles de Cocina")
    private String category;
    
    @Schema(description = "Descripción adicional del producto", example = "Mesa rectangular madera maciza 160x90cm")
    private String description;
}