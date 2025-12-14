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
@Table(name = "company")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "trade_name")
    private String tradeName;

    @Column(name = "tax_id")
    private String taxId;

    private String address;

    private String district;

    private String province;

    private String department;

    private String country;

    private String phone;

    private String email;

    private String website;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "economic_activity")
    private String economicActivity;

    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean hasValidTaxId() {
        return taxId != null;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        if (address != null)
            fullAddress.append(address);
        if (district != null)
            fullAddress.append(", ").append(district);
        if (province != null)
            fullAddress.append(", ").append(province);
        if (department != null)
            fullAddress.append(", ").append(department);
        if (country != null)
            fullAddress.append(", ").append(country);
        return fullAddress.toString();
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