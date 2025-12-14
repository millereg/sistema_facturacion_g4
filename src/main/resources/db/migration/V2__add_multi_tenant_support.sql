SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'company_id');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE users ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1', 
    'SELECT ''Column company_id already exists in users'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND INDEX_NAME = 'idx_users_company_id');
SET @sql = IF(@index_exists = 0, 
    'ALTER TABLE users ADD INDEX idx_users_company_id (company_id)', 
    'SELECT ''Index idx_users_company_id already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND CONSTRAINT_NAME = 'fk_users_company');
SET @sql = IF(@fk_exists = 0, 
    'ALTER TABLE users ADD CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES company(id)', 
    'SELECT ''FK fk_users_company already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product' AND COLUMN_NAME = 'company_id');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE product ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1', 
    'SELECT ''Column company_id already exists in product'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product' AND INDEX_NAME = 'idx_product_company_id');
SET @sql = IF(@index_exists = 0, 
    'ALTER TABLE product ADD INDEX idx_product_company_id (company_id)', 
    'SELECT ''Index idx_product_company_id already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product' AND CONSTRAINT_NAME = 'fk_product_company');
SET @sql = IF(@fk_exists = 0, 
    'ALTER TABLE product ADD CONSTRAINT fk_product_company FOREIGN KEY (company_id) REFERENCES company(id)', 
    'SELECT ''FK fk_product_company already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'customer' AND COLUMN_NAME = 'company_id');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE customer ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1', 
    'SELECT ''Column company_id already exists in customer'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'customer' AND INDEX_NAME = 'idx_customer_company_id');
SET @sql = IF(@index_exists = 0, 
    'ALTER TABLE customer ADD INDEX idx_customer_company_id (company_id)', 
    'SELECT ''Index idx_customer_company_id already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'customer' AND CONSTRAINT_NAME = 'fk_customer_company');
SET @sql = IF(@fk_exists = 0, 
    'ALTER TABLE customer ADD CONSTRAINT fk_customer_company FOREIGN KEY (company_id) REFERENCES company(id)', 
    'SELECT ''FK fk_customer_company already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'suppliers' AND COLUMN_NAME = 'company_id');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE suppliers ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1', 
    'SELECT ''Column company_id already exists in suppliers'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'suppliers' AND INDEX_NAME = 'idx_suppliers_company_id');
SET @sql = IF(@index_exists = 0, 
    'ALTER TABLE suppliers ADD INDEX idx_suppliers_company_id (company_id)', 
    'SELECT ''Index idx_suppliers_company_id already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'suppliers' AND CONSTRAINT_NAME = 'fk_suppliers_company');
SET @sql = IF(@fk_exists = 0, 
    'ALTER TABLE suppliers ADD CONSTRAINT fk_suppliers_company FOREIGN KEY (company_id) REFERENCES company(id)', 
    'SELECT ''FK fk_suppliers_company already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'categories' AND COLUMN_NAME = 'company_id');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE categories ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1', 
    'SELECT ''Column company_id already exists in categories'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'categories' AND INDEX_NAME = 'idx_categories_company_id');
SET @sql = IF(@index_exists = 0, 
    'ALTER TABLE categories ADD INDEX idx_categories_company_id (company_id)', 
    'SELECT ''Index idx_categories_company_id already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'categories' AND CONSTRAINT_NAME = 'fk_categories_company');
SET @sql = IF(@fk_exists = 0, 
    'ALTER TABLE categories ADD CONSTRAINT fk_categories_company FOREIGN KEY (company_id) REFERENCES company(id)', 
    'SELECT ''FK fk_categories_company already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'payments' AND COLUMN_NAME = 'company_id');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE payments ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1', 
    'SELECT ''Column company_id already exists in payments'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'payments' AND INDEX_NAME = 'idx_payments_company_id');
SET @sql = IF(@index_exists = 0, 
    'ALTER TABLE payments ADD INDEX idx_payments_company_id (company_id)', 
    'SELECT ''Index idx_payments_company_id already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'payments' AND CONSTRAINT_NAME = 'fk_payments_company');
SET @sql = IF(@fk_exists = 0, 
    'ALTER TABLE payments ADD CONSTRAINT fk_payments_company FOREIGN KEY (company_id) REFERENCES company(id)', 
    'SELECT ''FK fk_payments_company already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoices' AND COLUMN_NAME = 'company_id');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE invoices ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1', 
    'SELECT ''Column company_id already exists in invoices'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoices' AND INDEX_NAME = 'idx_invoices_company_id');
SET @sql = IF(@index_exists = 0, 
    'ALTER TABLE invoices ADD INDEX idx_invoices_company_id (company_id)', 
    'SELECT ''Index idx_invoices_company_id already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoices' AND CONSTRAINT_NAME = 'fk_invoices_company');
SET @sql = IF(@fk_exists = 0, 
    'ALTER TABLE invoices ADD CONSTRAINT fk_invoices_company FOREIGN KEY (company_id) REFERENCES company(id)', 
    'SELECT ''FK fk_invoices_company already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoice_details' AND COLUMN_NAME = 'company_id');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE invoice_details ADD COLUMN company_id BIGINT NOT NULL DEFAULT 1', 
    'SELECT ''Column company_id already exists in invoice_details'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoice_details' AND INDEX_NAME = 'idx_invoice_details_company_id');
SET @sql = IF(@index_exists = 0, 
    'ALTER TABLE invoice_details ADD INDEX idx_invoice_details_company_id (company_id)', 
    'SELECT ''Index idx_invoice_details_company_id already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoice_details' AND CONSTRAINT_NAME = 'fk_invoice_details_company');
SET @sql = IF(@fk_exists = 0, 
    'ALTER TABLE invoice_details ADD CONSTRAINT fk_invoice_details_company FOREIGN KEY (company_id) REFERENCES company(id)', 
    'SELECT ''FK fk_invoice_details_company already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoice_details' AND COLUMN_NAME = 'notes');
SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE invoice_details ADD COLUMN notes TEXT', 
    'SELECT ''Column notes already exists in invoice_details'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product' AND INDEX_NAME = 'idx_product_company_active');
SET @sql = IF(@index_exists = 0, 
    'CREATE INDEX idx_product_company_active ON product(company_id, active)', 
    'SELECT ''Index idx_product_company_active already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'customer' AND INDEX_NAME = 'idx_customer_company_active');
SET @sql = IF(@index_exists = 0, 
    'CREATE INDEX idx_customer_company_active ON customer(company_id, active)', 
    'SELECT ''Index idx_customer_company_active already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'suppliers' AND INDEX_NAME = 'idx_suppliers_company_active');
SET @sql = IF(@index_exists = 0, 
    'CREATE INDEX idx_suppliers_company_active ON suppliers(company_id, active)', 
    'SELECT ''Index idx_suppliers_company_active already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'categories' AND INDEX_NAME = 'idx_categories_company_active');
SET @sql = IF(@index_exists = 0, 
    'CREATE INDEX idx_categories_company_active ON categories(company_id, active)', 
    'SELECT ''Index idx_categories_company_active already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoices' AND INDEX_NAME = 'idx_invoices_company_status');
SET @sql = IF(@index_exists = 0, 
    'CREATE INDEX idx_invoices_company_status ON invoices(company_id, status)', 
    'SELECT ''Index idx_invoices_company_status already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'invoice_details' AND INDEX_NAME = 'idx_invoice_details_company_invoice');
SET @sql = IF(@index_exists = 0, 
    'CREATE INDEX idx_invoice_details_company_invoice ON invoice_details(company_id, invoice_id)', 
    'SELECT ''Index idx_invoice_details_company_invoice already exists'' AS info');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
