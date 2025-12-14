package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.SupplierDTO;
import java.util.List;
import java.util.Optional;

public interface SupplierService {
    SupplierDTO save(SupplierDTO supplier);

    Optional<SupplierDTO> findById(Long id);

    List<SupplierDTO> findAll();

    List<SupplierDTO> findAllActive();

    void deleteById(Long id);

    void activateSupplier(Long id);

    void deactivateSupplier(Long id);
}