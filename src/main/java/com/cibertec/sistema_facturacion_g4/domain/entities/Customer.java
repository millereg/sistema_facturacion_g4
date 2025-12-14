package com.cibertec.sistema_facturacion_g4.domain.entities;

import jakarta.persistence.*;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CustomerType type;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    private String phone;

    private String address;

    private String ruc;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "is_generic")
    private Boolean isGeneric;

    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CustomerType {
        PERSON("Persona Natural"),
        COMPANY("Empresa");

        private final String displayName;

        CustomerType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public boolean isGenericCustomer() {
        return Boolean.TRUE.equals(isGeneric);
    }

    public String getDisplayName() {
        if (type == CustomerType.COMPANY) {
            return businessName != null ? businessName : "Empresa sin nombre";
        } else {
            StringBuilder name = new StringBuilder();
            if (firstName != null)
                name.append(firstName);
            if (lastName != null) {
                if (name.length() > 0)
                    name.append(" ");
                name.append(lastName);
            }
            return name.length() > 0 ? name.toString() : "Cliente sin nombre";
        }
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
}
