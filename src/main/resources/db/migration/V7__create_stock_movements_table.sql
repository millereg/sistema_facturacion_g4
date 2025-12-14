-- Crear tabla de movimientos de stock
CREATE TABLE stock_movements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    movement_type VARCHAR(20) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    notes TEXT,
    movement_date DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_movements_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_movements_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Crear índices para mejorar el rendimiento de consultas
CREATE INDEX idx_stock_movements_product_id ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_movement_date ON stock_movements(movement_date);
CREATE INDEX idx_stock_movements_movement_type ON stock_movements(movement_type);
