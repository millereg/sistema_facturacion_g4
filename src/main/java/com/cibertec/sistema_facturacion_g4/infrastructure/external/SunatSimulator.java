package com.cibertec.sistema_facturacion_g4.infrastructure.external;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class SunatSimulator {
    public SunatSimulatorResponse enviarFactura(String ruc, String serie, String numero, String xmlContent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String codigoRespuesta = "0";
        String descripcion = "El Comprobante numero " + serie + "-" + numero +
                " ha sido aceptado";
        String hashSunat = "IZ" + UUID.randomUUID().toString().substring(0, 30).replace("-", "").toUpperCase();
        String xmlRespuesta = generarXmlRespuestaSimulado(ruc, serie, numero, codigoRespuesta, descripcion, hashSunat);

        return SunatSimulatorResponse.builder()
                .success(true)
                .codigoRespuesta(codigoRespuesta)
                .descripcionRespuesta(descripcion)
                .hashSunat(hashSunat)
                .xmlRespuesta(xmlRespuesta)
                .fechaProceso(LocalDateTime.now())
                .build();
    }

    private String generarXmlRespuestaSimulado(String ruc, String serie, String numero,
            String codigo, String descripcion, String hash) {
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <ar:ApplicationResponse xmlns:ar="urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2"
                                       xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
                                       xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2">
                    <cbc:UBLVersionID>2.0</cbc:UBLVersionID>
                    <cbc:CustomizationID>1.0</cbc:CustomizationID>
                    <cbc:ID>R-%s-%s</cbc:ID>
                    <cbc:IssueDate>%s</cbc:IssueDate>
                    <cbc:IssueTime>%s</cbc:IssueTime>
                    <cac:SenderParty>
                        <cac:PartyIdentification>
                            <cbc:ID>20123456789</cbc:ID>
                        </cac:PartyIdentification>
                        <cac:PartyName>
                            <cbc:Name>SUNAT</cbc:Name>
                        </cac:PartyName>
                    </cac:SenderParty>
                    <cac:ReceiverParty>
                        <cac:PartyIdentification>
                            <cbc:ID>%s</cbc:ID>
                        </cac:PartyIdentification>
                    </cac:ReceiverParty>
                    <cac:DocumentResponse>
                        <cac:Response>
                            <cbc:ResponseCode>%s</cbc:ResponseCode>
                            <cbc:Description>%s</cbc:Description>
                        </cac:Response>
                        <cac:DocumentReference>
                            <cbc:ID>%s-%s</cbc:ID>
                        </cac:DocumentReference>
                    </cac:DocumentResponse>
                    <ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#">
                        <ds:SignedInfo>
                            <ds:DigestValue>%s</ds:DigestValue>
                        </ds:SignedInfo>
                    </ds:Signature>
                </ar:ApplicationResponse>
                """
                .formatted(
                        serie, numero,
                        fechaHora.split("T")[0], fechaHora.split("T")[1],
                        ruc,
                        codigo, descripcion,
                        serie, numero,
                        hash);
    }

    public SunatSimulatorResponse consultarEstado(String ruc, String serie, String numero) {
        return SunatSimulatorResponse.builder()
                .success(true)
                .codigoRespuesta("0")
                .descripcionRespuesta("Comprobante encontrado y aceptado")
                .fechaProceso(LocalDateTime.now())
                .build();
    }
}