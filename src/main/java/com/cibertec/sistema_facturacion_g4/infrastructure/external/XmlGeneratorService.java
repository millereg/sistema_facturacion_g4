package com.cibertec.sistema_facturacion_g4.infrastructure.external;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class XmlGeneratorService {
    public String generarXmlFactura(String ruc, String razonSocial, String serie, String numero,
            String fechaEmision, String clienteDoc, String clienteNombre,
            BigDecimal subtotal, BigDecimal igv, BigDecimal total,
            String invoiceTypeCode) {
        String fechaFormateada = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String tipoCodigo = invoiceTypeCode != null && !invoiceTypeCode.isBlank() ? invoiceTypeCode : "01";

        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <Invoice xmlns="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"
                         xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"
                         xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2">

                    <cbc:UBLVersionID>2.1</cbc:UBLVersionID>
                    <cbc:CustomizationID>2.0</cbc:CustomizationID>
                    <cbc:ID>%s-%s</cbc:ID>
                    <cbc:IssueDate>%s</cbc:IssueDate>
                    <cbc:InvoiceTypeCode listID="0101">%s</cbc:InvoiceTypeCode>
                    <cbc:DocumentCurrencyCode>PEN</cbc:DocumentCurrencyCode>

                    <cac:AccountingSupplierParty>
                        <cac:Party>
                            <cac:PartyIdentification>
                                <cbc:ID schemeID="6">%s</cbc:ID>
                            </cac:PartyIdentification>
                            <cac:PartyName>
                                <cbc:Name>%s</cbc:Name>
                            </cac:PartyName>
                        </cac:Party>
                    </cac:AccountingSupplierParty>

                    <cac:AccountingCustomerParty>
                        <cac:Party>
                            <cac:PartyIdentification>
                                <cbc:ID schemeID="1">%s</cbc:ID>
                            </cac:PartyIdentification>
                            <cac:PartyName>
                                <cbc:Name>%s</cbc:Name>
                            </cac:PartyName>
                        </cac:Party>
                    </cac:AccountingCustomerParty>

                    <cac:TaxTotal>
                        <cbc:TaxAmount currencyID="PEN">%s</cbc:TaxAmount>
                    </cac:TaxTotal>

                    <cac:LegalMonetaryTotal>
                        <cbc:LineExtensionAmount currencyID="PEN">%s</cbc:LineExtensionAmount>
                        <cbc:TaxInclusiveAmount currencyID="PEN">%s</cbc:TaxInclusiveAmount>
                        <cbc:PayableAmount currencyID="PEN">%s</cbc:PayableAmount>
                    </cac:LegalMonetaryTotal>

                </Invoice>
                """.formatted(
                serie, numero,
                fechaFormateada,
                tipoCodigo,
                ruc, razonSocial,
                clienteDoc, clienteNombre,
                igv != null ? igv.toString() : "0.00",
                subtotal != null ? subtotal.toString() : "0.00",
                total != null ? total.toString() : "0.00",
                total != null ? total.toString() : "0.00");
    }
}