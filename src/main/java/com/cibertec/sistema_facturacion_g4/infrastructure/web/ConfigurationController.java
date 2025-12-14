package com.cibertec.sistema_facturacion_g4.infrastructure.web;

import com.cibertec.sistema_facturacion_g4.application.dto.SystemConfigurationDTO;
import com.cibertec.sistema_facturacion_g4.application.ports.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
public class ConfigurationController {
    private final ConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllConfigurations() {
        List<SystemConfigurationDTO> configs = configurationService.getAllConfigurations();

        Map<String, Object> result = new HashMap<>();
        result.put("configurations", configs);
        result.put("total", configs.size());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{key}")
    public ResponseEntity<SystemConfigurationDTO> getConfigByKey(@PathVariable String key) {
        SystemConfigurationDTO config = configurationService.getConfigByKey(key);
        return config != null ? ResponseEntity.ok(config) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<SystemConfigurationDTO> saveConfiguration(@RequestBody SystemConfigurationDTO dto) {
        SystemConfigurationDTO saved = configurationService.saveConfiguration(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/defaults")
    public ResponseEntity<Map<String, String>> getDefaultConfigurations() {
        Map<String, String> defaults = configurationService.getDefaultConfigurations();
        return ResponseEntity.ok(defaults);
    }

    @PostMapping("/init-defaults")
    public ResponseEntity<Map<String, Object>> initializeDefaults() {
        Map<String, Object> result = configurationService.initializeDefaults();
        return ResponseEntity.ok(result);
    }
}