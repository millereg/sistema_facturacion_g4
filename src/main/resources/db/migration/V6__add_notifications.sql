-- =====================================================
-- V6: Sistema de Notificaciones
-- =====================================================

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    user_id BIGINT,
    company_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_notification_company FOREIGN KEY (company_id) REFERENCES company(id),
    INDEX idx_notification_company_read (company_id, is_read),
    INDEX idx_notification_user (user_id),
    INDEX idx_notification_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE whatsapp_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    message_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    company_id BIGINT NOT NULL,
    sent_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    read_at TIMESTAMP NULL,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_whatsapp_company FOREIGN KEY (company_id) REFERENCES company(id),
    INDEX idx_whatsapp_company_status (company_id, status),
    INDEX idx_whatsapp_phone (phone_number),
    INDEX idx_whatsapp_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
