package com.cibertec.sistema_facturacion_g4.application.dto.pos;

import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.PaymentDTO;
import lombok.Data;
import java.util.List;

@Data
public class SaleRequest {
    private Long customerId;
    private String customerName;
    private String paymentMethod;
    private String invoiceType;
    private Double receivedAmount;
    private String notes;
    private List<SaleItemRequest> items;
    
    private InvoiceDTO invoice;
    private CustomerDTO customer;
    private List<ProductDTO> products;
    private List<PaymentDTO> payments;
}