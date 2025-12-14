package com.cibertec.sistema_facturacion_g4.application.mapper;

import com.cibertec.sistema_facturacion_g4.domain.entities.User;
import com.cibertec.sistema_facturacion_g4.application.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);

    @Mapping(target = "password", ignore = true)
    User toEntity(UserDTO userDTO);
}
