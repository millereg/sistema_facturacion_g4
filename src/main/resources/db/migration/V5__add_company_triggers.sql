DELIMITER $$

CREATE TRIGGER trg_company_after_insert
AFTER INSERT ON company
FOR EACH ROW
BEGIN
    INSERT INTO system_configuration (config_key, config_value, description, company_id, active, created_at) VALUES
    ('IGV_RATE', '0.18', 'IGV', NEW.id, TRUE, NOW()),
    ('DEFAULT_CURRENCY', 'PEN', 'Moneda', NEW.id, TRUE, NOW()),
    ('EXCHANGE_RATE_USD', '3.75', 'Tipo de cambio', NEW.id, TRUE, NOW()),
    ('LOW_STOCK_THRESHOLD', '5', 'Umbral de stock bajo', NEW.id, TRUE, NOW()),
    ('MAX_DISCOUNT_PERCENT', '20', 'Descuento máximo permitido %', NEW.id, TRUE, NOW()),
    ('INVOICE_EXPIRY_DAYS', '30', 'Días de vencimiento facturas', NEW.id, TRUE, NOW()),
    ('INVOICE_SERIES', 'F001', 'Serie por defecto para facturas', NEW.id, TRUE, NOW()),
    ('BOLETA_SERIES', 'B001', 'Serie por defecto para boletas', NEW.id, TRUE, NOW()),
    ('PRINT_FORMAT', 'A4', 'Formato de impresión', NEW.id, TRUE, NOW()),
    ('TAX_REGIME', 'GENERAL', 'Régimen tributario', NEW.id, TRUE, NOW()),
    ('DECIMAL_PLACES', '2', 'Decimales en montos', NEW.id, TRUE, NOW()),
    ('WHATSAPP_ENABLED', 'false', 'Activar envío de WhatsApp', NEW.id, TRUE, NOW()),
    ('WHATSAPP_PHONE_NUMBER_ID', '', 'Phone Number ID de WhatsApp Business', NEW.id, TRUE, NOW()),
    ('WHATSAPP_ACCESS_TOKEN', '', 'Access Token de WhatsApp Business API', NEW.id, TRUE, NOW()),
    ('WHATSAPP_API_VERSION', 'v17.0', 'Versión de WhatsApp Business API', NEW.id, TRUE, NOW());
    
    INSERT INTO document_series (series, document_type, current_number, company_id, active) VALUES
    ('F001', 'FACTURA', 0, NEW.id, TRUE),
    ('B001', 'BOLETA', 0, NEW.id, TRUE);
    
    -- Insertar cliente genérico para la empresa
    INSERT INTO customer (
        type, 
        document_type, 
        document_number, 
        business_name, 
        first_name, 
        last_name, 
        email, 
        phone, 
        address, 
        company_id, 
        is_generic, 
        active, 
        created_at, 
        updated_at
    ) VALUES (
        'PERSON',
        'DNI',
        '00000000',
        NULL,
        'Cliente',
        'Genérico',
        NULL,
        NULL,
        NULL,
        NEW.id,
        TRUE,
        TRUE,
        NOW(),
        NOW()
    );
END$$

DELIMITER ;
