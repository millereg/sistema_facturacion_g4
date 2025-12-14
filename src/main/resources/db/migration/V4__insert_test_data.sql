-- =====================================================
-- V4: Datos de Prueba para Sistema de Facturación
-- =====================================================

-- EMPRESA DE PRUEBA
INSERT INTO company (id, business_name, trade_name, tax_id, address, district, province, department, country, phone, email, website, logo_url, active, created_at, updated_at) 
VALUES 
(1, 'EMPRESA DEMO SAC', 'Demo Store', '20123456789', 'Av. Los Incas 123', 'San Isidro', 'Lima', 'Lima', 'Perú', '01-2345678', 'contacto@demo.com', 'www.demo.com', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'COMERCIAL PERÚ EIRL', 'Comercial Perú', '20987654321', 'Jr. Tacna 456', 'Cercado', 'Lima', 'Lima', 'Perú', '01-8765432', 'info@comercialperu.com', NULL, NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- CONFIGURACIONES DEL SISTEMA PARA EMPRESAS EXISTENTES
-- (El trigger solo funciona para empresas nuevas, estas ya existen)
-- Configuraciones Empresa 1
INSERT INTO system_configuration (config_key, config_value, description, company_id, active, created_at) VALUES
('IGV_RATE', '0.18', 'IGV', 1, TRUE, CURRENT_TIMESTAMP),
('DEFAULT_CURRENCY', 'PEN', 'Moneda', 1, TRUE, CURRENT_TIMESTAMP),
('EXCHANGE_RATE_USD', '3.75', 'Tipo de cambio', 1, TRUE, CURRENT_TIMESTAMP),
('LOW_STOCK_THRESHOLD', '5', 'Umbral de stock bajo', 1, TRUE, CURRENT_TIMESTAMP),
('MAX_DISCOUNT_PERCENT', '20', 'Descuento máximo permitido %', 1, TRUE, CURRENT_TIMESTAMP),
('INVOICE_EXPIRY_DAYS', '30', 'Días de vencimiento facturas', 1, TRUE, CURRENT_TIMESTAMP),
('INVOICE_SERIES', 'F001', 'Serie por defecto para facturas', 1, TRUE, CURRENT_TIMESTAMP),
('BOLETA_SERIES', 'B001', 'Serie por defecto para boletas', 1, TRUE, CURRENT_TIMESTAMP),
('PRINT_FORMAT', 'A4', 'Formato de impresión', 1, TRUE, CURRENT_TIMESTAMP),
('TAX_REGIME', 'GENERAL', 'Régimen tributario', 1, TRUE, CURRENT_TIMESTAMP),
('DECIMAL_PLACES', '2', 'Decimales en montos', 1, TRUE, CURRENT_TIMESTAMP),
('WHATSAPP_ENABLED', 'false', 'Activar envío de WhatsApp', 1, TRUE, CURRENT_TIMESTAMP),
('WHATSAPP_PHONE_NUMBER_ID', '', 'Phone Number ID de WhatsApp Business', 1, TRUE, CURRENT_TIMESTAMP),
('WHATSAPP_ACCESS_TOKEN', '', 'Access Token de WhatsApp Business API', 1, TRUE, CURRENT_TIMESTAMP),
('WHATSAPP_API_VERSION', 'v17.0', 'Versión de WhatsApp Business API', 1, TRUE, CURRENT_TIMESTAMP);

-- Configuraciones Empresa 2
INSERT INTO system_configuration (config_key, config_value, description, company_id, active, created_at) VALUES
('IGV_RATE', '0.18', 'IGV', 2, TRUE, CURRENT_TIMESTAMP),
('DEFAULT_CURRENCY', 'PEN', 'Moneda', 2, TRUE, CURRENT_TIMESTAMP),
('EXCHANGE_RATE_USD', '3.75', 'Tipo de cambio', 2, TRUE, CURRENT_TIMESTAMP),
('LOW_STOCK_THRESHOLD', '5', 'Umbral de stock bajo', 2, TRUE, CURRENT_TIMESTAMP),
('MAX_DISCOUNT_PERCENT', '15', 'Descuento máximo permitido %', 2, TRUE, CURRENT_TIMESTAMP),
('INVOICE_EXPIRY_DAYS', '30', 'Días de vencimiento facturas', 2, TRUE, CURRENT_TIMESTAMP),
('INVOICE_SERIES', 'F001', 'Serie por defecto para facturas', 2, TRUE, CURRENT_TIMESTAMP),
('BOLETA_SERIES', 'B001', 'Serie por defecto para boletas', 2, TRUE, CURRENT_TIMESTAMP),
('PRINT_FORMAT', 'A4', 'Formato de impresión', 2, TRUE, CURRENT_TIMESTAMP),
('TAX_REGIME', 'GENERAL', 'Régimen tributario', 2, TRUE, CURRENT_TIMESTAMP),
('DECIMAL_PLACES', '2', 'Decimales en montos', 2, TRUE, CURRENT_TIMESTAMP),
('WHATSAPP_ENABLED', 'false', 'Activar envío de WhatsApp', 2, TRUE, CURRENT_TIMESTAMP),
('WHATSAPP_PHONE_NUMBER_ID', '', 'Phone Number ID de WhatsApp Business', 2, TRUE, CURRENT_TIMESTAMP),
('WHATSAPP_ACCESS_TOKEN', '', 'Access Token de WhatsApp Business API', 2, TRUE, CURRENT_TIMESTAMP),
('WHATSAPP_API_VERSION', 'v17.0', 'Versión de WhatsApp Business API', 2, TRUE, CURRENT_TIMESTAMP);

-- USUARIOS DE PRUEBA
-- Password para todos: "password123" (encriptado con BCrypt)
INSERT INTO users (username, email, password, first_name, last_name, role, company_id, active, created_at, updated_at) 
VALUES 
('admin', 'admin@demo.com', '$2a$12$1ikeJfQei.YgO1x4xfpfvezxQLGh1rJQOX6x719sn/JOFt5Tb2ihG', 'Juan', 'Pérez', 'ADMIN', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('vendedor1', 'vendedor1@demo.com', '$2a$12$1ikeJfQei.YgO1x4xfpfvezxQLGh1rJQOX6x719sn/JOFt5Tb2ihG', 'María', 'González', 'VENDEDOR', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cajero1', 'cajero1@demo.com', '$2a$12$1ikeJfQei.YgO1x4xfpfvezxQLGh1rJQOX6x719sn/JOFt5Tb2ihG', 'Carlos', 'Rodríguez', 'CAJERO', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('gerente1', 'gerente1@demo.com', '$2a$12$1ikeJfQei.YgO1x4xfpfvezxQLGh1rJQOX6x719sn/JOFt5Tb2ihG', 'Ana', 'Torres', 'GERENTE', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin2', 'admin2@comercial.com', '$2a$12$1ikeJfQei.YgO1x4xfpfvezxQLGh1rJQOX6x719sn/JOFt5Tb2ihG', 'Luis', 'Martínez', 'ADMIN', 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- CATEGORÍAS
INSERT INTO categories (name, company_id, active, created_at, updated_at) 
VALUES 
('Muebles de Sala', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Muebles de Dormitorio', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Muebles de Oficina', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Muebles de Cocina', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Decoración', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Iluminación', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Colchones', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Muebles Infantiles', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Accesorios', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Ferretería', 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PROVEEDORES
INSERT INTO suppliers (name, document_number, phone, company_id, active, created_at, updated_at) 
VALUES 
('Muebles del Sur SAC', '20111222333', '01-4567890', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Importadora Muebles Finos EIRL', '20444555666', '01-7890123', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fabricaciones Lima SAC', '20777888999', '01-3456789', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Comercial Sur', '20123987654', '01-2468135', 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PRODUCTOS - Empresa 1
INSERT INTO product (code, name, description, price, currency, stock, category_id, supplier_id, company_id, active, created_at, updated_at) 
VALUES 
-- Muebles de Sala
('PROD001', 'Sofá Seccional 3 Cuerpos', 'Sofá en L tapizado tela chenille gris', 2499.00, 'PEN', 15, 1, 1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD002', 'Mesa de Centro Moderna', 'Mesa de centro madera y vidrio 100x60cm', 459.00, 'PEN', 25, 1, 1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD003', 'Sillón Individual Reclinable', 'Sillón relax con reposapiés', 899.00, 'PEN', 12, 1, 1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD004', 'Mueble TV 55"', 'Rack para TV con 2 cajones y compartimentos', 599.00, 'PEN', 18, 1, 1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Muebles de Dormitorio
('PROD005', 'Cama Queen Size', 'Cama de madera con cabecera tapizada', 1299.00, 'PEN', 10, 2, 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD006', 'Cómoda 6 Cajones', 'Cómoda melamina blanca 120x80cm', 589.00, 'PEN', 15, 2, 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD007', 'Velador Moderno', 'Mesa de noche con 2 cajones', 199.00, 'PEN', 30, 2, 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD008', 'Ropero 3 Puertas', 'Closet melamina con espejo 180x200cm', 1899.00, 'PEN', 8, 2, 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Muebles de Oficina
('PROD009', 'Escritorio Ejecutivo L', 'Escritorio esquinero melamina nogal 150x150cm', 899.00, 'PEN', 12, 3, 3, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD010', 'Silla Oficina Ergonómica', 'Silla giratoria con soporte lumbar', 449.00, 'PEN', 25, 3, 3, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD011', 'Archivador Metálico 4 Gavetas', 'Archivador vertical beige 132x47cm', 699.00, 'PEN', 10, 3, 3, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD012', 'Biblioteca Moderna', 'Estante 5 niveles melamina 180x80cm', 549.00, 'PEN', 15, 3, 3, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Muebles de Cocina
('PROD013', 'Mesa Comedor 6 Personas', 'Mesa rectangular madera maciza 160x90cm', 1199.00, 'PEN', 8, 4, 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD014', 'Silla Comedor Tapizada', 'Silla con respaldo alto tapizado (Precio unitario)', 189.00, 'PEN', 40, 4, 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD015', 'Mueble Cocina Base', 'Alacena inferior 120cm con 2 puertas', 459.00, 'PEN', 10, 4, 3, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD016', 'Mueble Cocina Aéreo', 'Alacena superior 100cm con puertas de vidrio', 329.00, 'PEN', 12, 4, 3, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Decoración
('PROD017', 'Espejo Decorativo Grande', 'Espejo marco dorado 120x80cm', 349.00, 'PEN', 20, 5, 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD018', 'Cuadro Decorativo Abstracto', 'Cuadro canvas 90x60cm', 159.00, 'PEN', 30, 5, 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD019', 'Cortina Blackout 2x2.5m', 'Cortina opaca térmica con argollas', 129.00, 'PEN', 45, 5, 2, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Iluminación
('PROD020', 'Lámpara de Pie Moderna', 'Lámpara trípode con pantalla tela', 289.00, 'PEN', 15, 6, 1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD021', 'Lámpara Techo LED Circular', 'Plafón LED 40W luz blanca 50cm', 199.00, 'PEN', 3, 6, 1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Colchones
('PROD022', 'Colchón Ortopédico Queen', 'Colchón resortes 160x200cm', 899.00, 'PEN', 2, 7, 1, 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PRODUCTOS - Empresa 2
INSERT INTO product (code, name, description, price, currency, stock, category_id, supplier_id, company_id, active, created_at, updated_at) 
VALUES 
('PROD101', 'Martillo 500g', 'Martillo de goma y acero', 35.00, 'PEN', 40, 10, 4, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROD102', 'Destornillador Set', 'Juego 6 destornilladores', 45.00, 'PEN', 25, 10, 4, 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- CLIENTES
-- Cliente genérico para empresa 1 (el trigger solo funciona para empresas nuevas)
INSERT INTO customer (type, document_type, document_number, business_name, first_name, last_name, email, phone, address, ruc, company_id, is_generic, active, created_at, updated_at) 
VALUES 
('PERSON', 'DNI', '00000000', NULL, 'Cliente', 'Genérico', NULL, NULL, NULL, NULL, 1, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cliente genérico para empresa 2
INSERT INTO customer (type, document_type, document_number, business_name, first_name, last_name, email, phone, address, ruc, company_id, is_generic, active, created_at, updated_at) 
VALUES 
('PERSON', 'DNI', '00000000', NULL, 'Cliente', 'Genérico', NULL, NULL, NULL, NULL, 2, TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Clientes normales empresa 1
INSERT INTO customer (type, document_type, document_number, business_name, first_name, last_name, email, phone, address, ruc, company_id, is_generic, active, created_at, updated_at) 
VALUES 
('PERSON', 'DNI', '12345678', NULL, 'Roberto', 'Sánchez', 'roberto.sanchez@email.com', '987654321', 'Av. Arequipa 1234', NULL, 1, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PERSON', 'DNI', '87654321', NULL, 'Patricia', 'López', 'patricia.lopez@email.com', '912345678', 'Jr. Lampa 567', NULL, 1, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('COMPANY', 'RUC', '20100200300', 'INVERSIONES LIMA SAC', NULL, NULL, 'compras@inversioneslima.com', '01-5678901', 'Av. Javier Prado 890', '20100200300', 1, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('COMPANY', 'RUC', '20300400500', 'CORPORACION PERU EIRL', NULL, NULL, 'ventas@corpperu.com', '01-9012345', 'Av. Colonial 2345', '20300400500', 1, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PERSON', 'DNI', '45678912', NULL, 'Jorge', 'Vargas', 'jorge.v@email.com', '998877665', 'Av. Brasil 456', NULL, 1, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PERSON', 'DNI', '78945612', NULL, 'Carmen', 'Flores', 'carmen.f@email.com', '987123456', 'Jr. Cusco 789', NULL, 1, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PERSON', 'DNI', '32165498', NULL, 'Miguel', 'Castro', 'miguel.c@email.com', '965874123', 'Av. Venezuela 321', NULL, 1, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('COMPANY', 'RUC', '20600700800', 'DISTRIBUIDORA NORTE SAC', NULL, NULL, 'info@distnorte.com', '01-4567123', 'Av. Universitaria 1111', '20600700800', 1, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Clientes empresa 2
('PERSON', 'DNI', '11223344', NULL, 'Fernando', 'Ríos', 'fernando.r@email.com', '977665544', 'Av. Grau 111', NULL, 2, FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- FACTURAS - Empresa 1
INSERT INTO invoices (number, series, type, status, issue_date, due_date, subtotal, tax_amount, discount_amount, total_amount, customer_id, customer_name, customer_document, user_id, user_name, payment_method, notes, company_id, active, created_at, updated_at)
VALUES
-- Factura 1: Venta de sofá
('00000001', 'F001', 'SALE', 'PAID', '2025-11-01 10:30:00', '2025-11-15 10:30:00', 2499.00, 449.82, 0.00, 2948.82, 1, 'Roberto Sánchez', '12345678', 2, 'María González', 'EFECTIVO', 'Primera venta del mes', 1, TRUE, '2025-11-01 10:30:00', '2025-11-01 10:30:00'),

-- Factura 2: Venta de muebles variados
('00000002', 'F001', 'SALE', 'PAID', '2025-11-05 14:20:00', '2025-11-19 14:20:00', 1176.00, 211.68, 0.00, 1387.68, 2, 'Patricia López', '87654321', 2, 'María González', 'TARJETA', NULL, 1, TRUE, '2025-11-05 14:20:00', '2025-11-05 14:20:00'),

-- Factura 3: Venta a empresa
('00000003', 'F001', 'SALE', 'ISSUED', '2025-11-10 09:15:00', '2025-12-10 09:15:00', 2597.00, 467.46, 0.00, 3064.46, 3, 'INVERSIONES LIMA SAC', '20100200300', 1, 'Juan Pérez', 'TRANSFERENCIA', 'Venta corporativa - 30 días crédito', 1, TRUE, '2025-11-10 09:15:00', '2025-11-10 09:15:00'),

-- Factura 4: Venta con descuento
('00000004', 'F001', 'SALE', 'PAID', '2025-11-15 16:45:00', '2025-11-29 16:45:00', 899.00, 160.82, 50.00, 1009.82, 5, 'Jorge Vargas', '45678912', 3, 'Carlos Rodríguez', 'EFECTIVO', 'Descuento por pronto pago', 1, TRUE, '2025-11-15 16:45:00', '2025-11-15 16:45:00'),

-- Factura 5: Venta grande pendiente de pago
('00000005', 'F001', 'SALE', 'ISSUED', '2025-11-20 11:00:00', '2025-12-20 11:00:00', 4995.00, 899.10, 0.00, 5894.10, 4, 'CORPORACION PERU EIRL', '20300400500', 1, 'Juan Pérez', 'TRANSFERENCIA', 'Compra al crédito', 1, TRUE, '2025-11-20 11:00:00', '2025-11-20 11:00:00'),

-- Factura 6: Venta reciente
('00000006', 'F001', 'SALE', 'PAID', '2025-11-25 13:30:00', '2025-12-09 13:30:00', 787.00, 141.66, 0.00, 928.66, 6, 'Carmen Flores', '78945612', 2, 'María González', 'TARJETA', NULL, 1, TRUE, '2025-11-25 13:30:00', '2025-11-25 13:30:00'),

-- Factura 7: Factura anulada
('00000007', 'F001', 'SALE', 'CANCELLED', '2025-11-27 10:00:00', '2025-12-11 10:00:00', 549.00, 98.82, 0.00, 647.82, 7, 'Miguel Castro', '32165498', 3, 'Carlos Rodríguez', 'EFECTIVO', 'Anulada por error en datos', 1, FALSE, '2025-11-27 10:00:00', '2025-11-27 10:00:00'),

-- Factura 8: Venta de hoy
('00000008', 'F001', 'SALE', 'PAID', '2025-11-29 09:00:00', '2025-12-13 09:00:00', 845.00, 152.10, 0.00, 997.10, 1, 'Roberto Sánchez', '12345678', 2, 'María González', 'EFECTIVO', 'Venta matutina', 1, TRUE, '2025-11-29 09:00:00', '2025-11-29 09:00:00');

-- DETALLES DE FACTURAS
-- Detalles Factura 1
INSERT INTO invoice_details (invoice_id, product_id, product_name, product_code, quantity, unit_price, discount, tax, total_amount)
VALUES
(1, 1, 'Sofá Seccional 3 Cuerpos', 'PROD001', 1, 2499.00, 0.00, 449.82, 2948.82);

-- Detalles Factura 2
INSERT INTO invoice_details (invoice_id, product_id, product_name, product_code, quantity, unit_price, discount, tax, total_amount)
VALUES
(2, 2, 'Mesa de Centro Moderna', 'PROD002', 1, 459.00, 0.00, 82.62, 541.62),
(2, 7, 'Velador Moderno', 'PROD007', 2, 199.00, 0.00, 71.64, 469.64),
(2, 18, 'Cuadro Decorativo Abstracto', 'PROD018', 1, 159.00, 0.00, 28.62, 187.62),
(2, 19, 'Cortina Blackout 2x2.5m', 'PROD019', 1, 129.00, 0.00, 23.22, 152.22);

-- Detalles Factura 3
INSERT INTO invoice_details (invoice_id, product_id, product_name, product_code, quantity, unit_price, discount, tax, total_amount)
VALUES
(3, 9, 'Escritorio Ejecutivo L', 'PROD009', 2, 899.00, 0.00, 323.64, 2121.64),
(3, 10, 'Silla Oficina Ergonómica', 'PROD010', 2, 449.00, 0.00, 161.64, 1059.64);

-- Detalles Factura 4
INSERT INTO invoice_details (invoice_id, product_id, product_name, product_code, quantity, unit_price, discount, tax, total_amount)
VALUES
(4, 3, 'Sillón Individual Reclinable', 'PROD003', 1, 899.00, 50.00, 152.82, 1001.82);

-- Detalles Factura 5
INSERT INTO invoice_details (invoice_id, product_id, product_name, product_code, quantity, unit_price, discount, tax, total_amount)
VALUES
(5, 1, 'Sofá Seccional 3 Cuerpos', 'PROD001', 1, 2499.00, 0.00, 449.82, 2948.82),
(5, 5, 'Cama Queen Size', 'PROD005', 1, 1299.00, 0.00, 233.82, 1532.82),
(5, 13, 'Mesa Comedor 6 Personas', 'PROD013', 1, 1199.00, 0.00, 215.82, 1414.82);

-- Detalles Factura 6
INSERT INTO invoice_details (invoice_id, product_id, product_name, product_code, quantity, unit_price, discount, tax, total_amount)
VALUES
(6, 6, 'Cómoda 6 Cajones', 'PROD006', 1, 589.00, 0.00, 106.02, 695.02),
(6, 7, 'Velador Moderno', 'PROD007', 2, 199.00, 0.00, 71.64, 469.64);

-- Detalles Factura 7 (anulada)
INSERT INTO invoice_details (invoice_id, product_id, product_name, product_code, quantity, unit_price, discount, tax, total_amount)
VALUES
(7, 12, 'Biblioteca Moderna', 'PROD012', 1, 549.00, 0.00, 98.82, 647.82);

-- Detalles Factura 8
INSERT INTO invoice_details (invoice_id, product_id, product_name, product_code, quantity, unit_price, discount, tax, total_amount)
VALUES
(8, 17, 'Espejo Decorativo Grande', 'PROD017', 1, 349.00, 0.00, 62.82, 411.82),
(8, 20, 'Lámpara de Pie Moderna', 'PROD020', 1, 289.00, 0.00, 52.02, 341.02),
(8, 14, 'Silla Comedor Tapizada', 'PROD014', 2, 189.00, 0.00, 68.04, 446.04);

-- PAGOS
INSERT INTO payments (invoice_id, amount, currency, method, payment_date, company_id)
VALUES
-- Pagos completos
(1, 2948.82, 'PEN', 'EFECTIVO', '2025-11-01 10:35:00', 1),
(2, 1387.68, 'PEN', 'TARJETA', '2025-11-05 14:25:00', 1),
(4, 1009.82, 'PEN', 'EFECTIVO', '2025-11-15 16:50:00', 1),
(6, 928.66, 'PEN', 'TARJETA', '2025-11-25 13:35:00', 1),
(8, 997.10, 'PEN', 'EFECTIVO', '2025-11-29 09:05:00', 1),

-- Pago parcial para factura pendiente
(3, 1500.00, 'PEN', 'TRANSFERENCIA', '2025-11-12 15:00:00', 1),
(5, 2000.00, 'PEN', 'TRANSFERENCIA', '2025-11-22 10:00:00', 1);

-- ACTUALIZAR NUMERACIÓN DE SERIES (las series ya fueron creadas arriba con los parámetros)
UPDATE document_series SET current_number = 8 WHERE series = 'F001' AND company_id = 1;
UPDATE document_series SET current_number = 0 WHERE series = 'B001' AND company_id = 1;
UPDATE document_series SET current_number = 0 WHERE series = 'F001' AND company_id = 2;
UPDATE document_series SET current_number = 0 WHERE series = 'B001' AND company_id = 2;
