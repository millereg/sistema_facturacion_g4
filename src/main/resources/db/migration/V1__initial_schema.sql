CREATE TABLE company (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_name VARCHAR(255),
    trade_name VARCHAR(255),
    tax_id VARCHAR(11),
    economic_activity VARCHAR(255),
    address VARCHAR(255),
    district VARCHAR(100),
    province VARCHAR(100),
    department VARCHAR(100),
    country VARCHAR(100) DEFAULT 'Perú',
    phone VARCHAR(50),
    email VARCHAR(255),
    website VARCHAR(255),
    logo_url VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50),
    company_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    image_url VARCHAR(500),
    company_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    phone VARCHAR(50),
    company_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_supplier_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    currency VARCHAR(10) DEFAULT 'PEN',
    stock INT DEFAULT 0,
    image_url VARCHAR(500),
    category_id BIGINT,
    supplier_id BIGINT,
    company_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_product_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20),
    document_type VARCHAR(20),
    document_number VARCHAR(20),
    business_name VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(50),
    address VARCHAR(255),
    ruc VARCHAR(11),
    company_id BIGINT NOT NULL,
    is_generic BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_company FOREIGN KEY (company_id) REFERENCES company(id),
    INDEX idx_customer_company_generic (company_id, is_generic)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(50),
    series VARCHAR(10),
    type VARCHAR(20),
    status VARCHAR(20),
    issue_date TIMESTAMP,
    due_date TIMESTAMP,
    subtotal DECIMAL(10,2),
    subtotal_currency VARCHAR(10) DEFAULT 'PEN',
    tax_amount DECIMAL(10,2),
    tax_currency VARCHAR(10) DEFAULT 'PEN',
    discount_amount DECIMAL(10,2),
    discount_currency VARCHAR(10) DEFAULT 'PEN',
    total_amount DECIMAL(10,2),
    total_currency VARCHAR(10) DEFAULT 'PEN',
    customer_id BIGINT,
    customer_name VARCHAR(255),
    customer_document VARCHAR(20),
    user_id BIGINT,
    user_name VARCHAR(255),
    payment_method VARCHAR(50),
    notes TEXT,
    company_id BIGINT NOT NULL,
    sent_to_sunat BOOLEAN DEFAULT FALSE,
    sunat_sent_at TIMESTAMP,
    sunat_response_code VARCHAR(50),
    sunat_hash VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_invoice_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_invoice_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE invoice_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT,
    product_name VARCHAR(255),
    product_code VARCHAR(50),
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2),
    discount DECIMAL(10,2) DEFAULT 0,
    tax DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2),
    CONSTRAINT fk_detail_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT fk_detail_product FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(10,2),
    currency VARCHAR(10) DEFAULT 'PEN',
    method VARCHAR(50) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    company_id BIGINT NOT NULL,
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    CONSTRAINT fk_payment_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE document_series (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_type VARCHAR(20) NOT NULL,
    series VARCHAR(10) NOT NULL,
    next_number BIGINT DEFAULT 1,
    company_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_series_company FOREIGN KEY (company_id) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de módulos del sistema
CREATE TABLE modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    route VARCHAR(255),
    parent_id BIGINT,
    display_order INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_module_parent FOREIGN KEY (parent_id) REFERENCES modules(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla de permisos
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    module_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'VIEW, CREATE, EDIT, DELETE',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_permission_module FOREIGN KEY (module_id) REFERENCES modules(id),
    INDEX idx_permission_code (code),
    INDEX idx_permission_module (module_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabla relacional: roles y permisos
CREATE TABLE role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_role_perm_role FOREIGN KEY (role_name) REFERENCES roles(name) ON DELETE CASCADE,
    CONSTRAINT fk_role_perm_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_permission (role_name, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO roles (name, description, active) VALUES
('ADMIN', 'Administrador del sistema', TRUE),
('GERENTE', 'Gerente general', TRUE),
('CAJERO', 'Cajero', TRUE);
