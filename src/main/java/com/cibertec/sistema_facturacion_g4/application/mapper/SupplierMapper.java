package com.cibertec.sistema_facturacion_g4.application.mapper;

import com.cibertec.sistema_facturacion_g4.domain.entities.Supplier;
import com.cibertec.sistema_facturacion_g4.application.dto.SupplierDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierDTO toDTO(Supplier supplier);
    
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "active", ignore = true)
    Supplier toEntity(SupplierDTO supplierDTO);
}