package com.cibertec.sistema_facturacion_g4.application.mapper;

import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceDTO toDTO(Invoice invoice);

    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "details", ignore = true)
    Invoice toEntity(InvoiceDTO invoiceDTO);
}
