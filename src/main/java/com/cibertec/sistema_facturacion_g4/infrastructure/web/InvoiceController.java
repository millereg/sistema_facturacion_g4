package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.InvoicePrintDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.InvoiceService;
import com.cibertec.sistema_facturacion_g4.infrastructure.external.SunatSimulator;
import com.cibertec.sistema_facturacion_g4.infrastructure.external.SunatSimulatorResponse;
import com.cibertec.sistema_facturacion_g4.infrastructure.external.XmlGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Facturas", description = "API para gestión de facturas y comprobantes")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final SunatSimulator sunatSimulator;
    private final XmlGeneratorService xmlGenerator;

    @Operation(summary = "Listar facturas", description = "Retorna todas las facturas de la empresa")
    @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    @Operation(summary = "Obtener factura por ID", description = "Retorna los detalles de una factura específica")
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getById(
            @Parameter(description = "ID de la factura") @PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        return invoice != null ? ResponseEntity.ok(invoice) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener factura para impresión", 
               description = "Retorna los datos formateados de una factura para su impresión")
    @GetMapping("/{id}/print")
    public ResponseEntity<InvoicePrintDTO> getInvoiceForPrint(
            @Parameter(description = "ID de la factura") @PathVariable Long id) {
        InvoicePrintDTO printData = invoiceService.getInvoiceForPrint(id);
        return ResponseEntity.ok(printData);
    }

    @Operation(summary = "Facturas por cliente", description = "Retorna todas las facturas de un cliente específico")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByCustomer(
            @Parameter(description = "ID del cliente") @PathVariable Long customerId) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByCustomer(customerId);
        return ResponseEntity.ok(invoices);
    }

    @Operation(summary = "Facturas pendientes de pago", description = "Retorna todas las facturas emitidas que aún no están pagadas")
    @ApiResponse(responseCode = "200", description = "Lista de facturas pendientes obtenida exitosamente")
    @GetMapping("/pending")
    public ResponseEntity<List<InvoiceDTO>> getPendingInvoices() {
        List<InvoiceDTO> invoices = invoiceService.getPendingInvoices();
        return ResponseEntity.ok(invoices);
    }

    @Operation(summary = "Anular factura", description = "Anula una factura emitida")
    @ApiResponse(responseCode = "200", description = "Factura anulada correctamente")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelInvoice(
            @Parameter(description = "ID de la factura") @PathVariable Long id,
            @Parameter(description = "Motivo de anulación") @RequestParam String reason) {
        boolean cancelled = invoiceService.cancelInvoice(id, reason);
        return cancelled ? ResponseEntity.ok("Factura anulada correctamente")
                : ResponseEntity.badRequest().body("No se pudo anular la factura");
    }

    @Operation(summary = "Reenviar factura", description = "Reenvía una factura al cliente")
    @PostMapping("/{id}/resend")
    public ResponseEntity<String> resendInvoice(
            @Parameter(description = "ID de la factura") @PathVariable Long id) {
        boolean sent = invoiceService.resendInvoice(id);
        return sent ? ResponseEntity.ok("Factura reenviada correctamente")
                : ResponseEntity.badRequest().body("No se pudo reenviar la factura");
    }

    @Operation(summary = "Enviar factura a SUNAT", 
               description = "Envía la factura electrónica a SUNAT para su validación")
    @PostMapping("/{id}/send-sunat")
    public ResponseEntity<Map<String, Object>> sendToSunat(
            @Parameter(description = "ID de la factura") @PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        if (invoice == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Factura no encontrada");
            return ResponseEntity.badRequest().body(error);
        }
            boolean isBoleta = (invoice.getType() != null && invoice.getType().equalsIgnoreCase("BOLETA"))
                || (invoice.getSeries() != null && invoice.getSeries().startsWith("B"));
            if (isBoleta) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Las boletas no generan XML electrónico en esta versión");
                return ResponseEntity.badRequest().body(error);
            }
            String tipoCodigo = "01";
        String xml = xmlGenerator.generarXmlFactura(
                "20123456789",
                "MI EMPRESA SAC",
                invoice.getSeries(),
                invoice.getNumber(),
                invoice.getIssueDate(),
                invoice.getCustomerDocument(),
                invoice.getCustomerName(),
                invoice.getSubtotal(),
            invoice.getTaxAmount(),
            invoice.getTotalAmount(),
            tipoCodigo);
        SunatSimulatorResponse response = sunatSimulator.enviarFactura(
                "20123456789",
                invoice.getSeries(),
                invoice.getNumber(),
                xml);
        Map<String, Object> result = new HashMap<>();
        result.put("success", response.isSuccess());
        result.put("message", response.getDescripcionRespuesta());
        result.put("sunatCode", response.getCodigoRespuesta());
        result.put("hash", response.getHashSunat());
        result.put("xmlGenerated", xml.length() > 0 ? "Sí" : "No");
        result.put("processingTime", "2 segundos");

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Obtener XML de factura", 
               description = "Genera y retorna el XML de una factura electrónica")
    @GetMapping("/{id}/xml")
    public ResponseEntity<Map<String, Object>> getInvoiceXml(
            @Parameter(description = "ID de la factura") @PathVariable Long id) {
        try {
            InvoiceDTO invoice = invoiceService.getInvoiceById(id);
            if (invoice == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isBoleta = (invoice.getType() != null && invoice.getType().equalsIgnoreCase("BOLETA"))
                || (invoice.getSeries() != null && invoice.getSeries().startsWith("B"));
            if (isBoleta) {
            Map<String, Object> result = new HashMap<>();
            result.put("invoiceId", id);
            result.put("series", invoice.getSeries());
            result.put("number", invoice.getNumber());
            result.put("generated", false);
            result.put("message", "Las boletas no generan XML electrónico en esta versión");
            return ResponseEntity.ok(result);
            }

            String tipoCodigo = "01";

                String xml = xmlGenerator.generarXmlFactura(
                    "20123456789",
                    "MI EMPRESA SAC",
                    invoice.getSeries(),
                    invoice.getNumber(),
                    invoice.getIssueDate(),
                    invoice.getCustomerDocument(),
                    invoice.getCustomerName(),
                    invoice.getSubtotal(),
                    invoice.getTaxAmount(),
                    invoice.getTotalAmount(),
                    tipoCodigo);

            Map<String, Object> result = new HashMap<>();
            result.put("invoiceId", id);
            result.put("series", invoice.getSeries());
            result.put("number", invoice.getNumber());
            result.put("xmlContent", xml);
            result.put("generated", true);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "No se pudo generar XML: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
