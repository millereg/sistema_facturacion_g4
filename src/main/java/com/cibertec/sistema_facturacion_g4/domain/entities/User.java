
package com.cibertec.sistema_facturacion_g4.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import com.cibertec.sistema_facturacion_g4.shared.constants.UserRoles;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserRoles role;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    private Boolean active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null)
            fullName.append(firstName);
        if (lastName != null) {
            if (fullName.length() > 0)
                fullName.append(" ");
            fullName.append(lastName);
        }
        return fullName.toString();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return UserRoles.ADMIN.equals(role);
    }
}