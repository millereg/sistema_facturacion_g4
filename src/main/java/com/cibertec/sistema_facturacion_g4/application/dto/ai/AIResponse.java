package com.cibertec.sistema_facturacion_g4.application.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta del asistente de IA")
public class AIResponse {
    
    @Schema(description = "Contenido generado por la IA", 
            example = "Sofá moderno de 3 plazas con diseño elegante...")
    private String content;
    
    @Schema(description = "Indica si la solicitud fue exitosa", example = "true")
    private boolean success;
    
    @Schema(description = "Mensaje de error (si aplica)", example = "null")
    private String errorMessage;
    
    public static AIResponse success(String content) {
        return new AIResponse(content, true, null);
    }
    
    public static AIResponse error(String errorMessage) {
        return new AIResponse(null, false, errorMessage);
    }
}
