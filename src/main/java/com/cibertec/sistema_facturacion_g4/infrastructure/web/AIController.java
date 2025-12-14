package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.ai.*;
import com.cibertec.sistema_facturacion_g4.application.usecases.AIAssistantServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "Asistente de IA", description = "Endpoints para funcionalidades de IA con Llama 3.2")
@SecurityRequirement(name = "bearerAuth")
public class AIController {

    private final AIAssistantServiceImpl aiAssistantService;

    @PostMapping("/product-description")
    @Operation(summary = "Generar descripción de producto con IA", description = "Genera una descripción profesional y atractiva para un producto usando Llama 3.2", responses = {
            @ApiResponse(responseCode = "200", description = "Descripción generada exitosamente", content = @Content(schema = @Schema(implementation = AIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error al conectar con el servicio de IA")
    })
    public ResponseEntity<AIResponse> generateProductDescription(
            @Valid @RequestBody ProductDescriptionRequest request) {

        AIResponse response = aiAssistantService.generateProductDescription(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/discount-suggestion")
    @Operation(summary = "Sugerir descuento inteligente", description = "Analiza ventas y stock para sugerir un descuento estratégico usando IA", responses = {
            @ApiResponse(responseCode = "200", description = "Sugerencia generada exitosamente", content = @Content(schema = @Schema(implementation = AIResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al generar sugerencia")
    })
    public ResponseEntity<AIResponse> suggestDiscount(
            @Valid @RequestBody DiscountSuggestionRequest request) {

        AIResponse response = aiAssistantService.suggestDiscount(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/product-price")
    @Operation(summary = "Sugerir precio de producto con IA", description = "Genera una sugerencia de precio realista para un producto usando IA", responses = {
            @ApiResponse(responseCode = "200", description = "Precio sugerido exitosamente", content = @Content(schema = @Schema(implementation = AIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "500", description = "Error al conectar con el servicio de IA")
    })
    public ResponseEntity<AIResponse> generateProductPrice(
            @Valid @RequestBody ProductPriceRequest request) {

        AIResponse response = aiAssistantService.generateProductPrice(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sales-trends")
    @Operation(summary = "Analizar tendencias de ventas", description = "Genera un análisis de tendencias de ventas en lenguaje natural usando IA", responses = {
            @ApiResponse(responseCode = "200", description = "Análisis generado exitosamente", content = @Content(schema = @Schema(implementation = AIResponse.class))),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido o sin datos"),
            @ApiResponse(responseCode = "500", description = "Error al generar análisis")
    })
    public ResponseEntity<AIResponse> analyzeSalesTrends(
            @Valid @RequestBody SalesTrendRequest request) {

        AIResponse response = aiAssistantService.analyzeSalesTrends(request);
        return ResponseEntity.ok(response);
    }
}
