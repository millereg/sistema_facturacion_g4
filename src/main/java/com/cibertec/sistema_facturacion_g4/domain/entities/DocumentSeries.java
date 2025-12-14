package com.cibertec.sistema_facturacion_g4.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_series")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4)
    private String series;

    @Column(name = "document_type", nullable = false, length = 10)
    private String documentType;

    @Column(name = "current_number", nullable = false)
    private Long currentNumber;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public String generateNextDocumentNumber() {
        currentNumber++;
        return series + "-" + String.format("%08d", currentNumber);
    }

    public String getCurrentDocumentNumber() {
        return series + "-" + String.format("%08d", currentNumber);
    }
}
