package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import java.util.List;
import java.util.Map;

public interface ConfigurationService {
    List<SystemConfigurationDTO> getAllConfigurations();

    SystemConfigurationDTO getConfigByKey(String key);

    SystemConfigurationDTO getConfigByKey(String key, Long companyId);

    SystemConfigurationDTO saveConfiguration(SystemConfigurationDTO dto);

    Map<String, String> getDefaultConfigurations();

    Map<String, Object> initializeDefaults();
}