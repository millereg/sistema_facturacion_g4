package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import com.cibertec.sistema_facturacion_g4.application.ports.InvoiceService;
import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.InvoicePrintDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.CustomerDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.ProductDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.InvoiceDetailDTO;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceDetailRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CompanyRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CustomerRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.ProductRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.PaymentRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.UserRepository;
import com.cibertec.sistema_facturacion_g4.application.mapper.InvoiceMapper;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.entities.InvoiceDetail;
import com.cibertec.sistema_facturacion_g4.domain.entities.Company;
import com.cibertec.sistema_facturacion_g4.domain.entities.Customer;
import com.cibertec.sistema_facturacion_g4.domain.entities.Product;
import com.cibertec.sistema_facturacion_g4.domain.entities.Payment;
import com.cibertec.sistema_facturacion_g4.domain.services.StockDomainService;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import com.cibertec.sistema_facturacion_g4.shared.utils.InvoiceNumberGenerator;
import com.cibertec.sistema_facturacion_g4.shared.utils.TaxCalculator;
import com.cibertec.sistema_facturacion_g4.shared.utils.NumberToWordsConverter;
import com.cibertec.sistema_facturacion_g4.infrastructure.security.SecurityUtils;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.BusinessException;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.InsufficientStockException;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.ValidationException;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.InvalidOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.sistema_facturacion_g4.infrastructure.email.EmailService;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;
    private final StockDomainService stockDomainService;
    private final InvoiceNumberGenerator invoiceNumberGenerator;
    private final TaxCalculator taxCalculator;
    private final NumberToWordsConverter numberToWordsConverter;
    private final ConfigurationService configurationService;
    private final EmailService emailService;

    @Override
    @Transactional
    public InvoiceDTO createInvoiceFromPOS(InvoiceDTO invoiceDTO, CustomerDTO customerDTO, List<ProductDTO> productsDTO,
            List<InvoiceDetailDTO> detailsDTO) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        if (detailsDTO == null || detailsDTO.isEmpty()) {
            throw new ValidationException("La factura debe tener al menos un detalle");
        }
        
        List<Product> products = new ArrayList<>();
        for (InvoiceDetailDTO detail : detailsDTO) {
            if (detail.getProductId() == null) {
                throw new ValidationException("El ID del producto es requerido");
            }
            if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
                throw new ValidationException("La cantidad debe ser mayor a 0");
            }
            
            Product product = productRepository.findById(detail.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + detail.getProductId()));
            
            if (!companyId.equals(product.getCompanyId())) {
                throw new InvalidOperationException("El producto no pertenece a la empresa actual");
            }
            
            if (!product.getActive()) {
                throw new InvalidOperationException("Producto inactivo: " + product.getName());
            }
            
            int requiredQuantity = detail.getQuantity();
            if (product.getStock() == null || product.getStock() < requiredQuantity) {
                throw new InsufficientStockException("Stock insuficiente para: " + product.getName() + 
                    ". Disponible: " + product.getStock() + ", Solicitado: " + requiredQuantity);
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
                log.warn("No se pudo obtener la serie desde configuración, usando valor por defecto: {}", defaultSeries);
                series = defaultSeries;
            }
        }

        Long lastCorrelative = invoiceRepository.findLastCorrelativeBySeries(series, companyId);
        Long correlative = lastCorrelative != null ? lastCorrelative + 1 : 1L;
        String invoiceNumber = String.format("%08d", correlative);
        invoice.setNumber(invoiceNumber);
        invoice.setSeries(series);
        
        invoice.setType(invoiceType);
        // Las boletas se emiten directamente (no van a SUNAT), las facturas empiezan como borrador
        if (invoiceType == Invoice.InvoiceType.BOLETA) {
            invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
            invoice.setSentToSunat(false); // Las boletas no se envían a SUNAT en este sistema
        } else {
            invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
            invoice.setSentToSunat(false);
        }
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setCreatedAt(LocalDateTime.now());
        
        // Calcular fecha de vencimiento (30 días por defecto)
        SystemConfigurationDTO expiryDaysConfig = configurationService.getConfigByKey("INVOICE_EXPIRY_DAYS");
        int expiryDays = 30; // valor por defecto
        if (expiryDaysConfig != null && expiryDaysConfig.getValue() != null) {
            try {
                expiryDays = Integer.parseInt(expiryDaysConfig.getValue());
            } catch (NumberFormatException e) {
                log.warn("Valor inválido para INVOICE_EXPIRY_DAYS, usando 30 días por defecto");
            }
        }
        invoice.setDueDate(LocalDateTime.now().plusDays(expiryDays));
        
        if (customerDTO != null && customerDTO.getId() != null) {
            invoice.setCustomerId(customerDTO.getId());
            invoice.setCustomerName(customerDTO.getFirstName() + " " + 
                    (customerDTO.getLastName() != null ? customerDTO.getLastName() : ""));
            invoice.setCustomerDocument(customerDTO.getDocumentNumber());
        } else {
            invoice.setCustomerName("Cliente General");
        }
        
        if (invoiceDTO.getUserId() != null) {
            invoice.setUserId(invoiceDTO.getUserId());
            invoice.setUserName("Usuario " + invoiceDTO.getUserId());
        }
        
        BigDecimal subtotal = invoiceDTO.getSubtotal() != null ? invoiceDTO.getSubtotal() : BigDecimal.ZERO;
        BigDecimal tax = invoiceDTO.getTaxAmount() != null ? invoiceDTO.getTaxAmount() : taxCalculator.calculateIGV(subtotal);
        BigDecimal discount = invoiceDTO.getDiscountAmount() != null ? invoiceDTO.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal total = invoiceDTO.getTotalAmount() != null ? invoiceDTO.getTotalAmount() : 
                taxCalculator.calculateTotal(subtotal, tax, discount);

        SystemConfigurationDTO currencyConfig = configurationService.getConfigByKey("DEFAULT_CURRENCY");
        String defaultCurrency = currencyConfig != null ? currencyConfig.getValue() : "PEN";
        
        invoice.setSubtotal(subtotal);
        invoice.setSubtotalCurrency(defaultCurrency);
        invoice.setTaxAmount(tax);
        invoice.setTaxCurrency(defaultCurrency);
        invoice.setDiscountAmount(discount);
        invoice.setDiscountCurrency(defaultCurrency);
        invoice.setTotalAmount(total);
        invoice.setTotalCurrency(defaultCurrency);
        invoice.setPaymentMethod(invoiceDTO.getPaymentMethod() != null ? invoiceDTO.getPaymentMethod() : "CASH");
        invoice.setNotes(invoiceDTO.getNotes());
        invoice.setActive(true);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        for (int i = 0; i < detailsDTO.size(); i++) {
            InvoiceDetailDTO detailDTO = detailsDTO.get(i);
            Product product = products.get(i);
            
            InvoiceDetail detail = new InvoiceDetail();
            detail.setCompanyId(companyId);
            detail.setInvoiceId(savedInvoice.getId());
            detail.setProductId(product.getId());
            detail.setProductCode(product.getCode());
            detail.setProductName(product.getName());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(detailDTO.getUnitPrice() != null ? detailDTO.getUnitPrice() : product.getPrice());
            detail.setDiscount(detailDTO.getDiscount() != null ? detailDTO.getDiscount() : BigDecimal.ZERO);
            detail.setTax(detailDTO.getTax() != null ? detailDTO.getTax() : BigDecimal.ZERO);
            detail.setTotalAmount(detailDTO.getTotalAmount() != null ? detailDTO.getTotalAmount() : BigDecimal.ZERO);
            
            invoiceDetailRepository.save(detail);
            
            int quantityToReduce = detailDTO.getQuantity();
            stockDomainService.registerStockMovement(product, quantityToReduce, "OUT");
            if (product.getStock() < 0) {
                throw new InsufficientStockException("Error en cálculo de stock para: " + product.getName());
            }
            productRepository.save(product);
        }

        if (invoiceDTO.getPaymentMethod() != null && !invoiceDTO.getPaymentMethod().isEmpty()) {
            Payment payment = new Payment();
            payment.setCompanyId(companyId);
            payment.setInvoiceId(savedInvoice.getId());
            
            BigDecimal paymentAmount = total;
            String notes = invoiceDTO.getNotes();
            if (notes != null && notes.startsWith("RECEIVED:")) {
                try {
                    int pipeIndex = notes.indexOf("|");
                    String receivedStr = notes.substring(9, pipeIndex > 0 ? pipeIndex : notes.length());
                    BigDecimal receivedAmount = new BigDecimal(receivedStr);
                    
                    paymentAmount = receivedAmount.compareTo(total) > 0 ? total : receivedAmount;
                    
                    if (pipeIndex > 0) {
                        savedInvoice.setNotes(notes.substring(pipeIndex + 1));
                        invoiceRepository.save(savedInvoice);
                    }
                } catch (Exception e) {
                    paymentAmount = total;
                }
            }
            
            payment.setAmount(paymentAmount);
            payment.setCurrency(defaultCurrency);
            payment.setMethod(invoiceDTO.getPaymentMethod());
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
        }

        return invoiceMapper.toDTO(savedInvoice);
    }

    @Override
    public List<InvoiceDTO> getAllInvoices() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        String role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();
        
        if (("CAJERO".equals(role) || "VENDEDOR".equals(role)) && userId != null) {
            return invoiceRepository.findByCompanyIdAndUserId(companyId, userId)
                    .stream()
                    .map(invoiceMapper::toDTO)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        return invoiceRepository.findByCompanyId(companyId)
                .stream()
                .map(invoiceMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public InvoiceDTO getInvoiceById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return invoiceRepository.findByIdAndCompanyId(id, companyId)
                .map(invoiceMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<InvoiceDTO> getInvoicesByCustomer(Long customerId) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return invoiceRepository.findByCompanyIdAndCustomerId(companyId, customerId)
                .stream()
                .map(invoiceMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> getPendingInvoices() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        String role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();
        
        List<Invoice> invoices;
        
        if (("CAJERO".equals(role) || "VENDEDOR".equals(role)) && userId != null) {
            invoices = invoiceRepository.findByCompanyIdAndUserId(companyId, userId);
        } else {
            invoices = invoiceRepository.findByCompanyId(companyId);
        }
        
        return invoices.stream()
                .filter(invoice -> invoice.getStatus() == Invoice.InvoiceStatus.ISSUED)
                .map(invoiceMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public boolean cancelInvoice(Long id, String reason) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        if (id == null) {
            throw new ValidationException("El ID de la factura es requerido");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("El motivo de anulación es requerido");
        }
        
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada"));
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            throw new InvalidOperationException("La factura ya está anulada");
        }
        
        List<InvoiceDetail> detalles = invoiceDetailRepository.findByInvoiceId(id);
        for (InvoiceDetail detalle : detalles) {
            Product producto = productRepository.findById(detalle.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + detalle.getProductId()));
            
            int cantidadDevolver = detalle.getQuantity() != null ? detalle.getQuantity() : 0;
            stockDomainService.registerStockMovement(producto, cantidadDevolver, "IN");
            productRepository.save(producto);
        }
        
        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        invoice.setNotes((invoice.getNotes() != null ? invoice.getNotes() + " | " : "") + "Anulada: " + reason);
        invoice.setUpdatedAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
        return true;
    }

    @Override
    public boolean resendInvoice(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new BusinessException("Factura no encontrada"));

        if (invoice.getCustomerId() == null) {
            throw new BusinessException("La factura no tiene un cliente asignado");
        }

        String email = customerRepository.findByIdAndCompanyId(invoice.getCustomerId(), companyId)
                .map(Customer::getEmail)
                .orElse(null);

        if (email == null || email.isBlank()) {
            throw new BusinessException("El cliente no tiene correo registrado");
        }

        emailService.sendInvoiceEmail(email, invoice);
        log.info("Factura {} reenviada por email a {}", invoice.getId(), email);
        return true;
    }

    @Override
    @Transactional
    public boolean markAsSentToSunat(Long id, String responseCode, String hash) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada"));
        
        invoice.setSentToSunat(true);
        invoice.setSunatSentAt(LocalDateTime.now());
        invoice.setSunatResponseCode(responseCode);
        invoice.setSunatHash(hash);
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        
        invoiceRepository.save(invoice);
        return true;
    }

    @Override
    public InvoicePrintDTO getInvoiceForPrint(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new BusinessException("Factura no encontrada"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new BusinessException("Empresa no encontrada"));
        Customer customer = null;
        if (invoice.getCustomerId() != null) {
            customer = customerRepository.findByIdAndCompanyId(invoice.getCustomerId(), companyId)
                    .orElse(null);
        }
        List<InvoiceDetail> details = invoiceDetailRepository.findByInvoiceId(id);
        InvoicePrintDTO.CompanyInfo companyInfo = InvoicePrintDTO.CompanyInfo.builder()
                .businessName(company.getBusinessName())
                .tradeName(company.getTradeName())
                .ruc(company.getTaxId() != null ? company.getTaxId() : "")
                .address(company.getAddress())
                .district(company.getDistrict())
                .province(company.getProvince())
                .department(company.getDepartment())
                .country(company.getCountry() != null ? company.getCountry() : "Perú")
                .phone(company.getPhone())
                .email(company.getEmail() != null ? company.getEmail() : "")
                .website(company.getWebsite())
                .logoUrl(company.getLogoUrl())
                .build();
        InvoicePrintDTO.DocumentInfo documentInfo = InvoicePrintDTO.DocumentInfo.builder()
                .type(invoice.getType() != null ? invoice.getType().toString() : "VENTA")
                .series(invoice.getSeries())
                .number(invoice.getNumber())
                .fullNumber(invoice.getSeries() + "-" + invoice.getNumber())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .status(invoice.getStatus() != null ? invoice.getStatus().toString() : "EMITIDA")
                .build();
        InvoicePrintDTO.CustomerInfo customerInfo = null;
        if (customer != null) {
            customerInfo = InvoicePrintDTO.CustomerInfo.builder()
                    .documentType(customer.getDocumentType())
                    .documentNumber(customer.getDocumentNumber())
                    .fullName(customer.getDisplayName())
                    .address(customer.getAddress())
                    .phone(customer.getPhone())
                    .email(customer.getEmail() != null ? customer.getEmail() : "")
                    .build();
        } else {
            customerInfo = InvoicePrintDTO.CustomerInfo.builder()
                    .documentNumber(invoice.getCustomerDocument())
                    .fullName(invoice.getCustomerName())
                    .build();
        }
        List<InvoicePrintDTO.InvoiceDetailInfo> detailsInfo = new ArrayList<>();
        int itemNumber = 1;

        for (InvoiceDetail detail : details) {
            Product product = null;
            if (detail.getProductId() != null) {
                product = productRepository.findByIdAndCompanyId(detail.getProductId(), companyId)
                        .orElse(null);
            }

            BigDecimal unitPrice = detail.getUnitPrice() != null ? detail.getUnitPrice() : BigDecimal.ZERO;
            Integer quantityInt = detail.getQuantity() != null ? detail.getQuantity() : 0;
            BigDecimal quantity = new BigDecimal(quantityInt);
            BigDecimal subtotal = unitPrice.multiply(quantity);
            BigDecimal discount = detail.getDiscount() != null ? detail.getDiscount() : BigDecimal.ZERO;
            BigDecimal total = subtotal.subtract(discount);

            InvoicePrintDTO.InvoiceDetailInfo detailInfo = InvoicePrintDTO.InvoiceDetailInfo.builder()
                    .item(itemNumber++)
                    .code(product != null ? product.getCode() : detail.getProductCode())
                    .description(detail.getProductName())
                    .quantity(quantityInt)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .discount(discount)
                    .total(total)
                    .build();

            detailsInfo.add(detailInfo);
        }
        BigDecimal subtotal = invoice.getSubtotal() != null ? invoice.getSubtotal() : BigDecimal.ZERO;
        BigDecimal discount = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxAmount = invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal total = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;

        InvoicePrintDTO.TotalsInfo totalsInfo = InvoicePrintDTO.TotalsInfo.builder()
                .subtotal(subtotal)
                .discount(discount)
                .taxRate(new BigDecimal("0.18"))
                .taxAmount(taxAmount)
                .total(total)
                .currency("PEN")
                .totalInWords(numberToWordsConverter.convertAmountToWords(total))
                .build();
        InvoicePrintDTO.AdditionalInfo additionalInfo = InvoicePrintDTO.AdditionalInfo.builder()
                .paymentMethod(invoice.getPaymentMethod())
                .userName(getEmployeeName(invoice.getUserId()))
                .notes(invoice.getNotes())
                .qrCode(null).hash(null).build();
        
        List<Payment> payments = paymentRepository.findByInvoiceIdAndCompanyId(id, companyId);
        BigDecimal paidAmount = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balanceDue = total.subtract(paidAmount);
        
        return InvoicePrintDTO.builder()
                .company(companyInfo)
                .document(documentInfo)
                .customer(customerInfo)
                .details(detailsInfo)
                .totals(totalsInfo)
                .additional(additionalInfo)
                .paidAmount(paidAmount)
                .balanceDue(balanceDue)
                .build();
    }
    
    private String getEmployeeName(Long userId) {
        if (userId == null) {
            return "Sistema";
        }
        
        return userRepository.findById(userId)
                .map(user -> {
                    String fullName = user.getFullName();
                    return (fullName != null && !fullName.isEmpty()) ? fullName : "Sistema";
                })
                .orElse("Sistema");
    }
}
