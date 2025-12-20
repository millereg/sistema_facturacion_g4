package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import com.cibertec.sistema_facturacion_g4.application.ports.SaleService;
import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.PaymentDTO;
import com.cibertec.sistema_facturacion_g4.application.mapper.InvoiceMapper;
import com.cibertec.sistema_facturacion_g4.application.mapper.PaymentMapper;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.entities.Payment;
import com.cibertec.sistema_facturacion_g4.domain.entities.Customer;
import com.cibertec.sistema_facturacion_g4.domain.entities.InvoiceDetail;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ProductRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.PaymentRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CustomerRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceDetailRepository;
import com.cibertec.sistema_facturacion_g4.domain.services.StockDomainService;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.InsufficientStockException;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.EntityNotFoundException;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.InvalidOperationException;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.ValidationException;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.BusinessException;
import com.cibertec.sistema_facturacion_g4.shared.utils.InvoiceNumberGenerator;
import com.cibertec.sistema_facturacion_g4.shared.utils.TaxCalculator;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final StockDomainService stockDomainService;
    private final InvoiceMapper invoiceMapper;
    private final PaymentMapper paymentMapper;
    private final InvoiceNumberGenerator invoiceNumberGenerator;
    private final TaxCalculator taxCalculator;
    private final ConfigurationService configurationService;

    @Override
    @Transactional
    public InvoiceDTO registerSale(InvoiceDTO invoiceDTO, CustomerDTO customerDTO, List<ProductDTO> productsDTO,
            List<PaymentDTO> paymentsDTO) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        if (productsDTO == null || productsDTO.isEmpty()) {
            throw new ValidationException("La venta debe tener al menos un producto");
        }
        
        List<Product> products = new ArrayList<>();
        for (ProductDTO productDTO : productsDTO) {
            if (productDTO.getId() == null) {
                throw new ValidationException("El ID del producto es requerido");
            }
            if (productDTO.getStock() == null || productDTO.getStock() <= 0) {
                throw new ValidationException("La cantidad debe ser mayor a 0");
            }
            
            Product product = productRepository.findById(productDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + productDTO.getId()));
            
            if (!companyId.equals(product.getCompanyId())) {
                throw new InvalidOperationException("El producto no pertenece a la empresa actual");
            }
            
            if (!product.getActive()) {
                throw new InvalidOperationException("Producto inactivo: " + product.getName());
            }
            
            if (product.getStock() == null || product.getStock() < productDTO.getStock()) {
                throw new InsufficientStockException("Stock insuficiente para: " + product.getName() + 
                    ". Disponible: " + product.getStock() + ", Solicitado: " + productDTO.getStock());
            }
            
            products.add(product);
        }
        
        Invoice invoice = new Invoice();
        invoice.setCompanyId(companyId);

        String requestedType = invoiceDTO.getType() != null ? invoiceDTO.getType().toUpperCase() : "SALE";
        Invoice.InvoiceType invoiceType = switch (requestedType) {
            case "BOLETA" -> Invoice.InvoiceType.BOLETA;
            case "PURCHASE" -> Invoice.InvoiceType.PURCHASE;
            default -> Invoice.InvoiceType.SALE;
        };

        if (customerDTO != null && customerDTO.getId() != null) {
            Customer customer = customerRepository.findByIdAndCompanyId(customerDTO.getId(), companyId)
                    .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado: " + customerDTO.getId()));
            
            if (Boolean.TRUE.equals(customer.getIsGeneric()) && invoiceType != Invoice.InvoiceType.BOLETA) {
                throw new BusinessException("El cliente genérico solo puede usar boletas");
            }
        }

        String configKey = invoiceType == Invoice.InvoiceType.BOLETA ? "BOLETA_SERIES" : "INVOICE_SERIES";
        String defaultSeries = invoiceType == Invoice.InvoiceType.BOLETA ? "B001" : "F001";
        String series = invoiceDTO.getSeries();
        
        if (series == null || series.isEmpty()) {
            try {
                SystemConfigurationDTO seriesConfig = configurationService.getConfigByKey(configKey);
                series = seriesConfig != null ? seriesConfig.getValue() : defaultSeries;
            } catch (Exception e) {
                series = defaultSeries;
            }
        }
        
        Long lastCorrelative = invoiceRepository.findLastCorrelativeBySeries(series, companyId);
        Long correlative = lastCorrelative != null ? lastCorrelative + 1 : 1L;
        String invoiceNumber = String.format("%08d", correlative);

        invoice.setNumber(invoiceNumber);
        invoice.setSeries(series);
        // Las boletas se emiten directamente, las facturas empiezan como borrador
        if (invoiceType == Invoice.InvoiceType.BOLETA) {
            invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        } else {
            invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        }
        invoice.setType(invoiceType);
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setCreatedAt(LocalDateTime.now());
        
        if (customerDTO != null && customerDTO.getId() != null) {
            invoice.setCustomerId(customerDTO.getId());
            invoice.setCustomerName(customerDTO.getFirstName() + " " + (customerDTO.getLastName() != null ? customerDTO.getLastName() : ""));
            invoice.setCustomerDocument(customerDTO.getDocumentNumber());
        }
        
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < productsDTO.size(); i++) {
            ProductDTO productDTO = productsDTO.get(i);
            BigDecimal lineTotal = taxCalculator.calculateSubtotal(productDTO.getPrice(), new BigDecimal(productDTO.getStock()));
            subtotal = subtotal.add(lineTotal);
        }
        
        BigDecimal tax = taxCalculator.calculateIGV(subtotal);
        BigDecimal total = taxCalculator.calculateTotal(subtotal, tax, BigDecimal.ZERO);
        
        SystemConfigurationDTO currencyConfig = configurationService.getConfigByKey("DEFAULT_CURRENCY");
        String defaultCurrency = currencyConfig != null ? currencyConfig.getValue() : "PEN";
        
        invoice.setSubtotal(subtotal);
        invoice.setSubtotalCurrency(defaultCurrency);
        invoice.setTaxAmount(tax);
        invoice.setTaxCurrency(defaultCurrency);
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setDiscountCurrency(defaultCurrency);
        invoice.setTotalAmount(total);
        invoice.setTotalCurrency(defaultCurrency);
        invoice.setPaymentMethod(invoiceDTO.getPaymentMethod() != null ? invoiceDTO.getPaymentMethod() : "CASH");
        invoice.setNotes(invoiceDTO.getNotes());
        invoice.setActive(true);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        for (int i = 0; i < productsDTO.size(); i++) {
            ProductDTO productDTO = productsDTO.get(i);
            Product product = products.get(i);
            
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoiceId(savedInvoice.getId());
            detail.setProductId(product.getId());
            detail.setProductCode(product.getCode());
            detail.setProductName(product.getName());
            detail.setQuantity(productDTO.getStock());
            detail.setUnitPrice(productDTO.getPrice());
            detail.setDiscount(BigDecimal.ZERO);
            BigDecimal lineSubtotal = taxCalculator.calculateSubtotal(productDTO.getPrice(), new BigDecimal(productDTO.getStock()));
            BigDecimal lineTax = taxCalculator.calculateIGV(lineSubtotal);
            detail.setTax(lineTax);
            detail.setTotalAmount(taxCalculator.calculateTotal(lineSubtotal, lineTax, BigDecimal.ZERO));
            invoiceDetailRepository.save(detail);
            
            stockDomainService.registerStockMovement(product, productDTO.getStock(), "OUT");
            if (product.getStock() < 0) {
                throw new InsufficientStockException("Error en cálculo de stock para: " + product.getName());
            }
            productRepository.save(product);
        }
        
        if (paymentsDTO != null && !paymentsDTO.isEmpty()) {
            for (PaymentDTO paymentDTO : paymentsDTO) {
                if (paymentDTO.getAmount() == null || paymentDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ValidationException("El monto del pago debe ser mayor a 0");
                }
                Payment payment = paymentMapper.toEntity(paymentDTO);
                payment.setInvoiceId(savedInvoice.getId());
                payment.setCurrency(defaultCurrency);
                payment.setPaymentDate(LocalDateTime.now());
                paymentRepository.save(payment);
            }
        }

        return invoiceMapper.toDTO(savedInvoice);
    }

    @Override
    @Transactional
    public boolean cancelSale(Long saleId, String reason) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        if (saleId == null) {
            throw new ValidationException("El ID de la venta es requerido");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("El motivo de anulación es requerido");
        }
        
        Invoice invoice = invoiceRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
        
        if (!companyId.equals(invoice.getCompanyId())) {
            throw new InvalidOperationException("La venta no pertenece a la empresa actual");
        }

        if (invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            throw new InvalidOperationException("La venta ya está anulada");
        }
        
        List<InvoiceDetail> details = invoiceDetailRepository.findByInvoiceId(saleId);
        for (InvoiceDetail detail : details) {
            Product product = productRepository.findById(detail.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + detail.getProductId()));
            
            int quantityToReturn = detail.getQuantity() != null ? detail.getQuantity().intValue() : 0;
            stockDomainService.registerStockMovement(product, quantityToReturn, "IN");
            productRepository.save(product);
        }
        
        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        invoice.setNotes((invoice.getNotes() != null ? invoice.getNotes() + " | " : "") + "Anulada: " + reason);
        invoice.setUpdatedAt(LocalDateTime.now());

        invoiceRepository.save(invoice);
        return true;
    }

    @Override
    public List<InvoiceDTO> getSalesByCustomer(Long customerId) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        if (customerId == null) {
            throw new ValidationException("El ID del cliente es requerido");
        }
        
        List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);
        return invoices.stream()
                .filter(f -> companyId.equals(f.getCompanyId()))
                .filter(f -> f.getType() == Invoice.InvoiceType.SALE || f.getType() == Invoice.InvoiceType.BOLETA)
                .filter(f -> f.getActive())
                .map(invoiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> getSalesByPeriod(String startDate, String endDate) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        List<Invoice> invoices = invoiceRepository.findByCompanyId(companyId);
        return invoices.stream()
                .filter(f -> f.getType() == Invoice.InvoiceType.SALE || f.getType() == Invoice.InvoiceType.BOLETA)
                .filter(f -> f.getIssueDate() != null)
                .filter(f -> f.getActive())
                .map(invoiceMapper::toDTO)
                .collect(Collectors.toList());
    }
}
