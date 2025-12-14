package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.PaymentDTO;
import com.cibertec.sistema_facturacion_g4.application.mapper.PaymentMapper;
import com.cibertec.sistema_facturacion_g4.application.ports.PaymentService;
import com.cibertec.sistema_facturacion_g4.domain.entities.Payment;
import com.cibertec.sistema_facturacion_g4.domain.entities.Invoice;
import com.cibertec.sistema_facturacion_g4.domain.repositories.PaymentRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.InvoiceRepository;
import com.cibertec.sistema_facturacion_g4.shared.exceptions.BusinessException;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import com.cibertec.sistema_facturacion_g4.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        paymentDTO.setCompanyId(companyId);
        paymentDTO.setPaymentDate(LocalDateTime.now().toString());
        
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(paymentDTO.getInvoiceId(), companyId)
                .orElseThrow(() -> new BusinessException("Factura no encontrada"));
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            throw new BusinessException("No se puede registrar pago para una factura cancelada");
        }
        
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment.setCompanyId(companyId);
        Payment saved = paymentRepository.save(payment);
        
        List<Payment> allPayments = paymentRepository.findByInvoiceIdAndCompanyId(
                paymentDTO.getInvoiceId(), companyId);
        BigDecimal totalPaid = allPayments.stream()
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (invoice.getTotalAmount() != null && totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
            log.info("Factura {} marcada como PAGADA. Total pagado: {}, Total factura: {}",
                    invoice.getId(), totalPaid, invoice.getTotalAmount());
        }
        
        PaymentDTO result = paymentMapper.toDTO(saved);
        result.setInvoiceCode(invoice.getSeries() + "-" + invoice.getNumber());
        return result;
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        Payment payment = paymentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new BusinessException("Pago no encontrado"));
        
        return paymentMapper.toDTO(payment);
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        String role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();
        
        List<Payment> payments;
        
        if (("CAJERO".equals(role) || "VENDEDOR".equals(role)) && userId != null) {
            payments = paymentRepository.findByCompanyIdAndUserId(companyId, userId);
        } else {
            payments = paymentRepository.findByCompanyId(companyId);
        }
        
        return payments.stream()
                .map(payment -> {
                    PaymentDTO dto = paymentMapper.toDTO(payment);
                    invoiceRepository.findByIdAndCompanyId(payment.getInvoiceId(), companyId)
                            .ifPresent(invoice -> {
                                dto.setInvoiceCode(invoice.getSeries() + "-" + invoice.getNumber());
                            });
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByInvoice(Long invoiceId) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        return paymentRepository.findByInvoiceIdAndCompanyId(invoiceId, companyId)
                .stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        return paymentRepository.findByCompanyIdAndPaymentDateBetween(companyId, startDate, endDate)
                .stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        Payment payment = paymentRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new BusinessException("Pago no encontrado"));
        
        Long invoiceId = payment.getInvoiceId();
        BigDecimal deletedAmount = payment.getAmount();
        
        Invoice invoice = invoiceRepository.findByIdAndCompanyId(invoiceId, companyId)
                .orElseThrow(() -> new BusinessException("Factura no encontrada"));
        
        paymentRepository.delete(payment);
        
        List<Payment> remainingPayments = paymentRepository.findByInvoiceIdAndCompanyId(invoiceId, companyId);
        BigDecimal totalPaidAfterDeletion = remainingPayments.stream()
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (invoice.getTotalAmount() != null
                && totalPaidAfterDeletion.compareTo(invoice.getTotalAmount()) < 0
                && invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
            invoiceRepository.save(invoice);
            log.info("Pago {} eliminado. Factura {} revertida de PAGADA a EMITIDA. Total pagado: {} < Total factura: {}",
                    id, invoice.getId(), totalPaidAfterDeletion, invoice.getTotalAmount());
        } else {
            log.info("Pago {} eliminado. Factura {} mantiene estado PAGADA. Total pagado: {}",
                    id, invoice.getId(), totalPaidAfterDeletion);
        }
    }
}
