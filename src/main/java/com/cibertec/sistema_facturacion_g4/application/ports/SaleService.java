package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.PaymentDTO;

import java.util.List;

public interface SaleService {
    InvoiceDTO registerSale(InvoiceDTO invoiceDTO, CustomerDTO customerDTO, List<ProductDTO> productsDTO,
            List<PaymentDTO> paymentsDTO);

    boolean cancelSale(Long saleId, String reason);

    List<InvoiceDTO> getSalesByCustomer(Long customerId);

    List<InvoiceDTO> getSalesByPeriod(String startDate, String endDate);
}
