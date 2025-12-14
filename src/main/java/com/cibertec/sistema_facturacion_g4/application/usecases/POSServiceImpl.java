package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.*;
import com.cibertec.sistema_facturacion_g4.application.dto.pos.*;
import com.cibertec.sistema_facturacion_g4.application.ports.*;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import com.cibertec.sistema_facturacion_g4.application.mapper.CustomerMapper;
import com.cibertec.sistema_facturacion_g4.application.mapper.ProductMapper;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.entities.Customer;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ProductRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CustomerRepository;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.EntityNotFoundException;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.ValidationException;
import com.cibertec.sistema_facturacion_g4.shared.utils.TaxCalculator;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class POSServiceImpl implements POSService {
    private final InvoiceService invoiceService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final TaxCalculator taxCalculator;
    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;
    private final ConfigurationService configurationService;

    @Override
    @Transactional
    public SaleResponse processSale(SaleRequest saleRequest, Long userId) {
        if (saleRequest == null || saleRequest.getItems() == null || saleRequest.getItems().isEmpty()) {
            return new SaleResponse(false, "La venta debe tener al menos un producto", null, null);
        }
        
        if (userId == null) {
            throw new ValidationException("El ID de usuario es requerido");
        }
        
        List<ProductDTO> products = new ArrayList<>();
        List<InvoiceDetailDTO> details = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
            
        for (SaleItemRequest item : saleRequest.getItems()) {
            if (item.getProductId() == null) {
                return new SaleResponse(false, "ID de producto requerido", null, null);
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return new SaleResponse(false, "La cantidad debe ser mayor a 0", null, null);
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return new SaleResponse(false, "El precio debe ser mayor a 0", null, null);
            }
            
            ProductDTO product = productService.findById(item.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
                
            if (product.getStock() == null || product.getStock() < item.getQuantity()) {
                return new SaleResponse(false, "Stock insuficiente para: " + product.getName() + 
                    ". Disponible: " + product.getStock(), null, null);
            }
            
            products.add(product);
            InvoiceDetailDTO detail = new InvoiceDetailDTO();
            detail.setProductId(item.getProductId());
            detail.setProductCode(item.getProductCode());
            detail.setProductName(item.getProductName());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setDiscount(BigDecimal.ZERO);
            
            BigDecimal lineSubtotal = taxCalculator.calculateSubtotal(item.getUnitPrice(), new BigDecimal(item.getQuantity()));
            BigDecimal lineTax = taxCalculator.calculateIGV(lineSubtotal);
            detail.setTax(lineTax);
            detail.setTotalAmount(taxCalculator.calculateTotal(lineSubtotal, lineTax, BigDecimal.ZERO));
            
            subtotal = subtotal.add(lineSubtotal);
            details.add(detail);
        }
        
        CustomerDTO customer = new CustomerDTO();
        if (saleRequest.getCustomerId() != null) {
            customer = customerService.findById(saleRequest.getCustomerId()).orElse(customer);
        } else {
            customer.setFirstName(saleRequest.getCustomerName() != null ? saleRequest.getCustomerName() : "Cliente");
            customer.setType(com.cibertec.sistema_facturacion_g4.domain.entities.Customer.CustomerType.PERSON);
        }
        
        BigDecimal igv = taxCalculator.calculateIGV(subtotal);
        BigDecimal total = taxCalculator.calculateTotal(subtotal, igv, BigDecimal.ZERO);
        
        SystemConfigurationDTO currencyConfig = configurationService.getConfigByKey("DEFAULT_CURRENCY");
        String defaultCurrency = currencyConfig != null ? currencyConfig.getValue() : "PEN";
        
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setType(saleRequest.getInvoiceType() != null ? saleRequest.getInvoiceType() : "SALE");
        invoice.setStatus("ISSUED");
        invoice.setCustomerName(customer.getFirstName());
        invoice.setUserId(userId);
        invoice.setPaymentMethod(saleRequest.getPaymentMethod() != null ? saleRequest.getPaymentMethod() : "EFECTIVO");
        invoice.setNotes(saleRequest.getNotes());
        invoice.setSubtotal(subtotal);
        invoice.setSubtotalCurrency(defaultCurrency);
        invoice.setTaxAmount(igv);
        invoice.setTaxCurrency(defaultCurrency);
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setDiscountCurrency(defaultCurrency);
        invoice.setTotalAmount(total);
        invoice.setTotalCurrency(defaultCurrency);
        
        if (saleRequest.getReceivedAmount() != null) {
            String originalNotes = invoice.getNotes() != null ? invoice.getNotes() : "";
            invoice.setNotes("RECEIVED:" + saleRequest.getReceivedAmount() + "|" + originalNotes);
        }
        
        InvoiceDTO processedInvoice = invoiceService.createInvoiceFromPOS(invoice, customer, products, details);

        return new SaleResponse(true, "Venta procesada correctamente", 
                              processedInvoice.getNumber(), processedInvoice.getTotalAmount());
    }

    @Override
    public List<ProductDTO> searchProducts(String searchTerm) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        List<Product> prods = productRepository.findByCompanyId(companyId);

        return prods.stream()
            .filter(p -> p.getActive() && p.getStock() != null && p.getStock() > 0)
            .filter(p -> searchTerm == null || 
                        p.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        p.getCode().toLowerCase().contains(searchTerm.toLowerCase()))
            .map(productMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findProductByCode(String code) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        
        List<Product> products = productRepository.findByCompanyId(companyId);
        Product product = products.stream()
            .filter(p -> code.equals(p.getCode()) && p.getActive())
            .findFirst()
            .orElse(null);
            
        return product != null ? productMapper.toDTO(product) : null;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String searchTerm) {
        List<Customer> customers = customerRepository.findAll();
        
        return customers.stream()
            .filter(c -> c.getActive())
            .filter(c -> searchTerm == null || 
                        c.getDisplayName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        (c.getDocumentNumber() != null && c.getDocumentNumber().contains(searchTerm)))
            .map(customerMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return customerService.findById(id).orElse(null);
    }
}