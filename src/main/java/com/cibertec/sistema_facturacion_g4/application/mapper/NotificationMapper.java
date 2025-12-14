package com.cibertec.sistema_facturacion_g4.application.mapper;

import com.cibertec.sistema_facturacion_g4.application.dto.NotificationDTO;
import com.cibertec.sistema_facturacion_g4.domain.entities.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDTO toDTO(Notification notification);

    Notification toEntity(NotificationDTO notificationDTO);
}
