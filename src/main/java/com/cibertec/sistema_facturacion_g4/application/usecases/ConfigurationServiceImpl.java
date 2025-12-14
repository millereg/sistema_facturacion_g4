package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import com.cibertec.sistema_facturacion_g4.domain.entities.SystemConfiguration;
import com.cibertec.sistema_facturacion_g4.domain.repositories.SystemConfigurationRepository;
import com.cibertec.sistema_facturacion_g4.shared.utils.TenantUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {
    private final SystemConfigurationRepository configRepository;
    
    @Override
    public List<SystemConfigurationDTO> getAllConfigurations() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        List<SystemConfiguration> configs = configRepository.findByCompanyId(companyId);
        
        return configs.stream()
                .map(this::toDTO)
                .toList();
    }
    
    @Override
    public SystemConfigurationDTO getConfigByKey(String key) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        return getConfigByKey(key, companyId);
    }

    @Override
    public SystemConfigurationDTO getConfigByKey(String key, Long companyId) {
        return configRepository.findByKeyAndCompanyId(key, companyId)
                .map(this::toDTO)
                .orElse(null);
    }
    
    @Override
    public SystemConfigurationDTO saveConfiguration(SystemConfigurationDTO dto) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        SystemConfiguration config = configRepository.findByKeyAndCompanyId(dto.getKey(), companyId)
                .orElse(SystemConfiguration.builder()
                        .key(dto.getKey())
                        .companyId(companyId)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .build());
        
        config.setValue(dto.getValue());
        config.setDescription(dto.getDescription());
        config.setUpdatedAt(LocalDateTime.now());
        
        SystemConfiguration saved = configRepository.save(config);
        return toDTO(saved);
    }
    
    @Override
    public Map<String, String> getDefaultConfigurations() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("IGV_RATE", "0.18");
        defaults.put("DEFAULT_CURRENCY", "PEN");
        defaults.put("EXCHANGE_RATE_USD", "3.75");
        defaults.put("LOW_STOCK_THRESHOLD", "5");
        defaults.put("MAX_DISCOUNT_PERCENT", "20");
        defaults.put("INVOICE_EXPIRY_DAYS", "30");
        defaults.put("INVOICE_SERIES", "F001");
        defaults.put("BOLETA_SERIES", "B001");
        defaults.put("PRINT_FORMAT", "A4");
        defaults.put("TAX_REGIME", "GENERAL");
        defaults.put("DECIMAL_PLACES", "2");
        defaults.put("WHATSAPP_ENABLED", "false");
        defaults.put("WHATSAPP_PHONE_NUMBER_ID", "");
        defaults.put("WHATSAPP_ACCESS_TOKEN", "");
        defaults.put("WHATSAPP_API_VERSION", "v17.0");
        
        return defaults;
    }
    
    @Override
    public Map<String, Object> initializeDefaults() {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        Map<String, String> defaults = getDefaultConfigurations();
        int created = 0;
        
        for (Map.Entry<String, String> entry : defaults.entrySet()) {
            if (!configRepository.findByKeyAndCompanyId(entry.getKey(), companyId).isPresent()) {
                SystemConfiguration config = SystemConfiguration.builder()
                        .key(entry.getKey())
                        .value(entry.getValue())
                        .description("Configuración por defecto")
                        .companyId(companyId)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .build();
                        
                configRepository.save(config);
                created++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Configuraciones inicializadas");
        result.put("created", created);
        
        return result;
    }
    
    private SystemConfigurationDTO toDTO(SystemConfiguration config) {
        SystemConfigurationDTO dto = new SystemConfigurationDTO();
        dto.setId(config.getId());
        dto.setKey(config.getKey());
        dto.setValue(config.getValue());
        dto.setDescription(config.getDescription());
        dto.setCompanyId(config.getCompanyId());
        dto.setActive(config.getActive());
        dto.setCreatedAt(config.getCreatedAt().toString());
        dto.setUpdatedAt(config.getUpdatedAt() != null ? config.getUpdatedAt().toString() : null);
        return dto;
    }
}