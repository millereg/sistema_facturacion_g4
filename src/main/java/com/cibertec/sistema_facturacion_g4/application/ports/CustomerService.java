package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerDTO save(CustomerDTO customer);

    Optional<CustomerDTO> findById(Long id);

    List<CustomerDTO> findAll();

    void deleteById(Long id);

    Optional<CustomerDTO> getGenericCustomer();
}
