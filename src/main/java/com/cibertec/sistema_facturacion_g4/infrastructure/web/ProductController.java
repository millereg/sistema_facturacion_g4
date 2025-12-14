package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.ImportExportService;
import com.cibertec.sistema_facturacion_g4.application.ports.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestión de productos e inventario")
public class ProductController {
    private final ProductService productService;
    private final ImportExportService importExportService;

    @Operation(summary = "Listar productos", description = "Retorna todos los productos de la empresa actual")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAll() {
        List<ProductDTO> productos = productService.findAll();
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en el inventario")
    @ApiResponse(responseCode = "200", description = "Producto creado exitosamente")
    @PostMapping
    public ResponseEntity<ProductDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del producto") @Valid @RequestBody ProductDTO dto) {
        ProductDTO saved = productService.save(dto);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(
            @Parameter(description = "ID del producto") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Nuevos datos del producto") @Valid @RequestBody ProductDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(productService.save(dto));
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto del sistema")
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Importar productos", description = "Importa productos desde un archivo Excel")
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importProducts(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = importExportService.importProducts(file);
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Operation(summary = "Exportar productos", description = "Exporta todos los productos a CSV")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportProducts() {
        try {
            // Generar CSV directamente aquí
            StringBuilder csv = new StringBuilder();
            csv.append("codigo,nombre,descripcion,precio,stock,activo\n");
            
            // Obtener productos de la empresa actual
            // Esto debería ser implementado en el ProductService
            List<ProductDTO> products = productService.findAll();
            
            for (ProductDTO product : products) {
                csv.append(String.format("%s,%s,%s,%.2f,%d,%s\n",
                    escapeCSV(product.getCode() != null ? product.getCode() : ""),
                    escapeCSV(product.getName() != null ? product.getName() : ""),
                    escapeCSV(product.getDescription() != null ? product.getDescription() : ""),
                    product.getPrice() != null ? product.getPrice() : 0.0,
                    product.getStock() != null ? product.getStock() : 0,
                    product.getActive() != null && product.getActive() ? "SI" : "NO"
                ));
            }
            
            // Agregar BOM UTF-8 para correcta codificación
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] result = new byte[bom.length + csvBytes.length];
            System.arraycopy(bom, 0, result, 0, bom.length);
            System.arraycopy(csvBytes, 0, result, bom.length, csvBytes.length);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"productos_" + System.currentTimeMillis() + ".csv\"")
                    .body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener plantilla de productos", description = "Genera plantilla CSV para importación")
    @GetMapping("/template")
    public ResponseEntity<byte[]> getProductTemplate() {
        try {
            StringBuilder csv = new StringBuilder();
            csv.append("codigo,nombre,descripcion,precio,stock\n");
            csv.append("PROD001,Laptop HP,Laptop HP Pavilion,1500.00,10\n");
            csv.append("PROD002,Mouse Logitech,Mouse optico inalambrico,25.50,50\n");
            
            // Agregar BOM UTF-8 para correcta codificación
            byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] result = new byte[bom.length + csvBytes.length];
            System.arraycopy(bom, 0, result, 0, bom.length);
            System.arraycopy(csvBytes, 0, result, bom.length, csvBytes.length);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"plantilla_productos.csv\"")
                    .body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
