package com.cibertec.sistema_facturacion_g4.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private String series;

    @Enumerated(EnumType.STRING)
    private InvoiceType type;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Column(name = "issue_date")
    private LocalDateTime issueDate;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    private BigDecimal subtotal;
    
    @Column(name = "tax_amount")
    private BigDecimal taxAmount;
    
    @Column(name = "discount_amount")
    private BigDecimal discountAmount;
    
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "customer_id")
    private Long customerId;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "customer_document")
    private String customerDocument;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "user_name")
    private String userName;

    @Column(name = "subtotal_currency")
    private String subtotalCurrency;

    @Column(name = "tax_currency")
    private String taxCurrency;

    @Column(name = "discount_currency")
    private String discountCurrency;

    @Column(name = "total_currency")
    private String totalCurrency;

    @Column(name = "payment_method")
    private String paymentMethod;

    private String notes;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "sent_to_sunat")
    private Boolean sentToSunat = false;

    @Column(name = "sunat_sent_at")
    private LocalDateTime sunatSentAt;

    @Column(name = "sunat_response_code")
    private String sunatResponseCode;

    @Column(name = "sunat_hash")
    private String sunatHash;

    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private List<InvoiceDetail> details;

    public enum InvoiceType {
        SALE,
        BOLETA,
        PURCHASE
    }

    public enum InvoiceStatus {
        DRAFT,
        ISSUED,
        PARTIALLY_PAID,
        PAID,
        CANCELLED,
        OVERDUE
    }
}