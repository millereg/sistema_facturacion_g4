package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.InvoicePrintDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDetailDTO;
import java.util.List;

public interface InvoiceService {
    InvoiceDTO createInvoiceFromPOS(InvoiceDTO invoice, CustomerDTO customer, List<ProductDTO> products,
            List<InvoiceDetailDTO> details);

    List<InvoiceDTO> getAllInvoices();

    InvoiceDTO getInvoiceById(Long id);

    List<InvoiceDTO> getInvoicesByCustomer(Long customerId);

    List<InvoiceDTO> getPendingInvoices();

    boolean cancelInvoice(Long id, String reason);

    boolean resendInvoice(Long id);

    InvoicePrintDTO getInvoiceForPrint(Long id);
}
