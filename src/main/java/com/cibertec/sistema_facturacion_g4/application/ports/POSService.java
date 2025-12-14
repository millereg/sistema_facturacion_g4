package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.pos.*;
import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import java.util.List;

public interface POSService {
    SaleResponse processSale(SaleRequest saleRequest, Long userId);

    List<ProductDTO> searchProducts(String searchTerm);

    ProductDTO findProductByCode(String code);

    List<CustomerDTO> searchCustomers(String searchTerm);

    CustomerDTO getCustomerById(Long id);
}