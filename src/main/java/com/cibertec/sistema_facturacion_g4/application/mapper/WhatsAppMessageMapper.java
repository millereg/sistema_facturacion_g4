package com.cibertec.sistema_facturacion_g4.application.mapper;

import com.cibertec.sistema_facturacion_g4.application.dto.WhatsAppMessageDTO;
import com.cibertec.sistema_facturacion_g4.domain.entities.WhatsAppMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WhatsAppMessageMapper {
    WhatsAppMessageDTO toDTO(WhatsAppMessage whatsAppMessage);

    WhatsAppMessage toEntity(WhatsAppMessageDTO whatsAppMessageDTO);
}
