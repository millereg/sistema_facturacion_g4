package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.CompanyDTO;
import java.util.List;
import java.util.Optional;

public interface CompanyService {
    CompanyDTO save(CompanyDTO company);

    Optional<CompanyDTO> findById(Long id);

    List<CompanyDTO> findAll();

    void deleteById(Long id);
}
