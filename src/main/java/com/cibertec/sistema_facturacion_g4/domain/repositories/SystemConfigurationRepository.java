package com.cibertec.sistema_facturacion_g4.domain.repositories;

import com.cibertec.sistema_facturacion_g4.domain.entities.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
    Optional<SystemConfiguration> findByKeyAndCompanyId(String key, Long companyId);

    List<SystemConfiguration> findByCompanyId(Long companyId);

    List<SystemConfiguration> findByCompanyIdAndActiveTrue(Long companyId);
}