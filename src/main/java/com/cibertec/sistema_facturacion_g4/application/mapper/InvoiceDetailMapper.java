package com.cibertec.sistema_facturacion_g4.application.mapper;

import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDetailDTO;
import com.cibertec.sistema_facturacion_g4.domain.entities.InvoiceDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceDetailMapper {
    InvoiceDetailDTO toDTO(InvoiceDetail invoiceDetail);

    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "notes", ignore = true)
    InvoiceDetail toEntity(InvoiceDetailDTO invoiceDetailDTO);
}
