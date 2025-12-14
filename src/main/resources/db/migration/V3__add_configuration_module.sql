CREATE TABLE IF NOT EXISTS document_series (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    series VARCHAR(4) NOT NULL,
    document_type VARCHAR(10) NOT NULL,
    company_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_series_company (series, company_id),
    FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'document_series' AND COLUMN_NAME = 'current_number');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE document_series ADD COLUMN current_number BIGINT NOT NULL DEFAULT 0 AFTER document_type', 
    'SELECT ''Column current_number already exists in document_series'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS system_configuration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL,
    config_value VARCHAR(500) NOT NULL,
    description VARCHAR(255),
    company_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_config_company (config_key, company_id),
    FOREIGN KEY (company_id) REFERENCES company(id),
    INDEX idx_config_key (config_key),
    INDEX idx_company (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Los índices ya están incluidos en la definición de la tabla system_configuration
-- idx_config_key y idx_company ya existen en la creación de la tabla

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'document_series' AND INDEX_NAME = 'idx_document_series_company');
SET @sql = IF(@index_exists = 0, 
    'CREATE INDEX idx_document_series_company ON document_series(company_id, active)', 
    'SELECT ''Index idx_document_series_company already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
