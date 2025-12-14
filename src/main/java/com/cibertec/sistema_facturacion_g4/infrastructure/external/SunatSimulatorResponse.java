package com.cibertec.sistema_facturacion_g4.infrastructure.external;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SunatSimulatorResponse {
    private boolean success;
    private String codigoRespuesta;
    private String descripcionRespuesta;
    private String hashSunat;
    private String xmlRespuesta;
    private LocalDateTime fechaProceso;
    private String numeroTicket;

    public boolean isAceptado() {
        return success && "0".equals(codigoRespuesta);
    }

    public boolean isRechazado() {
        return !success || (codigoRespuesta != null && codigoRespuesta.startsWith("4"));
    }
}