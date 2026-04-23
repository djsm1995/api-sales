-- 1. TABLA DE USUARIOS
CREATE TABLE users (
      id BINARY(16) PRIMARY KEY COMMENT 'Identificador único universal (UUID) del usuario',
      username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Nombre de usuario único para inicio de sesión',
      password VARCHAR(255) NOT NULL COMMENT 'Hash de la contraseña (BCrypt)',
      email VARCHAR(100) COMMENT 'Correo electrónico institucional o personal',
      first_name VARCHAR(100) COMMENT 'Nombre de usuario',
      last_name VARCHAR(100) COMMENT 'Apellido de usuario',
      dni CHAR(8) UNIQUE COMMENT 'DNI de usuario (8 caracteres exactos)',
      role VARCHAR(20) NOT NULL COMMENT 'Rol asignado: ADMIN, CASHIER, USER',
      is_active BOOLEAN DEFAULT TRUE COMMENT 'Estado lógico de la cuenta: 1 para activo, 0 para inactivo',
      created_at TIMESTAMP NULL COMMENT 'Fecha y hora de creación del registro',
      updated_at TIMESTAMP NULL COMMENT 'Fecha y hora de la última actualización del perfil o credenciales',
      last_login TIMESTAMP NULL COMMENT 'Registro del último inicio de sesión exitoso (útil para detectar cuentas inactivas o sospechosas)'
) COMMENT='Gestión de usuarios y credenciales de acceso al sistema';

-- 2. TABLA DE CATEGORÍAS
CREATE TABLE categories (
      id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador numérico autoincremental',
      name VARCHAR(100) NOT NULL COMMENT 'Nombre de la categoría (ej: Calzado, Repostería, Consultas, Puertas)',
      description TEXT COMMENT 'Descripción detallada de la categoría',
      is_active BOOLEAN DEFAULT TRUE COMMENT 'Estado de la categoría',
      created_at TIMESTAMP NULL COMMENT 'Fecha de creación',
      updated_at TIMESTAMP NULL COMMENT 'Fecha de última modificación'
) COMMENT='Clasificación de productos y servicios';

-- 3. UNIDADES DE MEDIDA (Crucial para que sea genérico: Par, Kilo, Unidad)
CREATE TABLE unit_measures (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(50) NOT NULL COMMENT 'Nombre: Kilo, Unidad, Par, Metro, Litro',
        abbreviation VARCHAR(10) NOT NULL COMMENT 'UND, KG, PR, MT, LTS',
        allows_decimal BOOLEAN DEFAULT FALSE COMMENT '1: Permite decimales (Peso/Volumen), 0: Solo enteros (Unidades/Pares)'
) COMMENT='Define cómo se cuenta el producto';

-- 4. TABLA DE PRODUCTOS
CREATE TABLE products (
    -- IDENTIFICADORES Y CODIGOS
        id BINARY(16) PRIMARY KEY COMMENT 'UUID único del producto',
        sku VARCHAR(50) UNIQUE COMMENT 'Código único de inventario (Stock Keeping Unit)',
        barcode VARCHAR(100) COMMENT 'CODIGO DE BARRAS',
    -- INFORMACIÓN COMERCIAL
        name VARCHAR(255) NOT NULL COMMENT 'Nombre comercial del producto o servicio',
        description VARCHAR(100),
        image_url VARCHAR(500),
    -- CLASIFICACIÓN Y MÉTRICA (RELACIONES)
        category_id BIGINT NOT NULL COMMENT 'Referencia a la tabla de categorías',
        unit_measure_id BIGINT NOT NULL COMMENT 'Referencia a la unidad de medida (Kilo, Par, Unidad). Define la métrica de conteo del producto.',
    -- VALORES MONETARIOS
        unit_price DECIMAL(19,4) NOT NULL COMMENT 'Precio unitario con alta precisión (4 decimales) para evitar errores de redondeo en cálculos de IGV y descuentos',
        currency VARCHAR(3) DEFAULT 'PEN' COMMENT 'Código de moneda ISO (PEN, USD)',
        apply_igv BOOLEAN DEFAULT TRUE COMMENT 'Indica si el precio está afecto al 18% de IGV',
    -- ESTADOS Y AUDITORÍA
        status ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO',
        created_at TIMESTAMP NULL COMMENT 'Fecha de registro en el catálogo',
        created_by BINARY(16) COMMENT 'UUID del usuario que registró el producto',
        updated_at TIMESTAMP NULL COMMENT 'Fecha de la última modificación del producto',
        updated_by BINARY(16) COMMENT 'UUID del último usuario que modificó el registro',
    -- RESTRICCIONES (INTEGRIDAD REFERENCIAL)
        CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
        CONSTRAINT fk_product_unit_measure FOREIGN KEY (unit_measure_id) REFERENCES unit_measures(id),
        CONSTRAINT fk_product_creator FOREIGN KEY (created_by) REFERENCES users(id)
        -- CONSTRAINT chk_unit_price CHECK (unit_price >= 0) -- Seguridad: No precios negativos
) COMMENT='Catálogo maestro de productos y servicios';

-- 5. TABLA CLIENTES
CREATE TABLE customers (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID único del cliente',
        document_type ENUM('DNI', 'RUC', 'CE') NOT NULL COMMENT 'Tipo de documento legal',
        document_number VARCHAR(20) UNIQUE NOT NULL COMMENT 'Número de documento (DNI de 8 o RUC de 11)',
        full_name VARCHAR(255) NOT NULL COMMENT 'Nombre completo o Razón Social',
        address TEXT COMMENT 'Dirección para entrega o facturación',
        phone VARCHAR(20) COMMENT 'Teléfono de contacto',
        email VARCHAR(100) COMMENT 'Email para envío de comprobante electrónico',
        is_active BOOLEAN DEFAULT TRUE COMMENT 'Estado del cliente',
        created_at TIMESTAMP NULL COMMENT 'Fecha de registro del cliente'
) COMMENT='Base de datos de clientes para facturación y fidelización';

-- 6. TABLA DE VENTAS (CABECERA)
CREATE TABLE sales (
        id BINARY(16) PRIMARY KEY COMMENT 'UUID único de la transacción de venta',
        user_id BINARY(16) COMMENT 'Referencia al usuario (vendedor) que realizó la venta',
        customer_id BIGINT COMMENT 'Referencia al cliente (DNI/RUC) para la factura',
        subtotal DECIMAL(19,4) NOT NULL COMMENT 'Monto antes de impuestos',
        igv_total DECIMAL(19,4) NOT NULL COMMENT 'Monto total de impuestos calculados',
        total_amount DECIMAL(19,4) NOT NULL COMMENT 'Monto total final de la venta',
        payment_method VARCHAR(30) COMMENT 'Método de pago: EFECTIVO, TARJETA, YAPE, PLIN',
        status VARCHAR(20) DEFAULT 'COMPLETED' COMMENT 'Estado de la venta: COMPLETED, CANCELLED, REFUNDED',
        created_at TIMESTAMP NULL COMMENT 'Fecha y hora exacta de la transacción',
        series CHAR(4) COMMENT 'Serie del comprobante (ej: B001)',
        number INT COMMENT 'Correlativo del comprobante',
        igv_percentage DECIMAL(5,2) DEFAULT 18.00 COMMENT 'Porcentaje de IGV aplicado (por si cambia la ley)',
        sale_type ENUM('BOLETA', 'FACTURA', 'NOTA_VENTA') DEFAULT 'NOTA_VENTA',
        CONSTRAINT fk_sale_user FOREIGN KEY (user_id) REFERENCES users(id),
        CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
        -- CONSTRAINT chk_total_positive CHECK (total_amount >= 0)
) COMMENT='Registro principal de transacciones comerciales';
-- 7. TABLA DE DETALLE DE VENTA
CREATE TABLE sale_items (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID único de la línea de detalle',
        sale_id BINARY(16) COMMENT 'Referencia a la cabecera de la venta',
        product_id BINARY(16) COMMENT 'Referencia al producto vendido',
        quantity DECIMAL(12,3) NOT NULL COMMENT 'Cantidad vendida (permite decimales para peso/fracción)',
        unit_price DECIMAL(19,4) NOT NULL COMMENT 'Precio unitario histórico al momento de la venta',
        discount_amount DECIMAL(19,4) DEFAULT 0.00 COMMENT 'Monto de descuento aplicado directamente a esta línea de producto (antes de impuestos)',
        total_item DECIMAL(19,4) NOT NULL COMMENT 'quantity * unit_price - discount',
        CONSTRAINT fk_item_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
        CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES products(id)
        -- CONSTRAINT chk_qty_positive CHECK (quantity > 0)
) COMMENT='Detalle desglosado de los productos incluidos en cada venta';

-- 8. TABLA DE AUDITORÍA
CREATE TABLE audit_log (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID único del registro de auditoría',
        entity_name VARCHAR(50) COMMENT 'Nombre de la tabla afectada',
        entity_id VARCHAR(50) COMMENT 'ID del registro específico que fue modificado',
        action VARCHAR(20) COMMENT 'Acción realizada: CREATE, UPDATE, DELETE',
        old_value TEXT COMMENT 'Estado anterior del registro (opcional)',
        new_value TEXT COMMENT 'Estado nuevo tras el cambio (opcional)',
        performed_by BINARY(16) NOT NULL COMMENT 'UUID del usuario que realizó la acción',
        created_at TIMESTAMP NULL COMMENT 'Momento exacto del cambio',
        ip_address VARCHAR(45) COMMENT 'Dirección IP de origen desde donde se realizó la petición (Soporta IPv4 e IPv6)',
        user_agent TEXT COMMENT 'Identificador del cliente o navegador (Browser, Mobile App, o procesos automáticos como Apache Camel)'
) COMMENT='Bitácora de seguridad para el rastreo de modificaciones en el sistema';

-- 8. TABLA ALMACEN
CREATE TABLE inventory (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID único del registro de inventario',
        product_id BINARY(16) NOT NULL COMMENT 'Relación con el UUID del producto',
        current_stock DECIMAL(12,3) NOT NULL DEFAULT 0 COMMENT 'Stock físico actual. Soporta decimales (ej: 0.500 kg de torta)',
        min_stock DECIMAL(12,3) DEFAULT 5 COMMENT 'Stock mínimo para alertas de reposición',
        location VARCHAR(100) COMMENT 'Ubicación física: Estante A, Almacén Principal, Vitrina',
        batch_number VARCHAR(50) COMMENT 'Número de lote (importante para tortas/perecederos)',
        expiration_date DATE NULL COMMENT 'Fecha de vencimiento (opcional)',
        is_available BOOLEAN DEFAULT TRUE COMMENT 'Indica si el stock está apto para la venta',
    -- Campos de Trazabilidad y Auditoría (Sin duplicados)
        created_by BINARY(16) NOT NULL COMMENT 'Usuario que ingreso el producto al inventario',
        modified_by BINARY(16) NULL COMMENT 'ID del último usuario que modificó el stock (ej: por venta o ajuste)',
        created_at TIMESTAMP NULL COMMENT 'Fecha de creación del registro',
        updated_at TIMESTAMP NULL COMMENT 'Última actualización general',
        last_entry_date TIMESTAMP NULL COMMENT 'Última entrada de mercadería (compras/abastecimiento)',
        CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id)
) COMMENT='Gestión de stock físico, ubicaciones y trazabilidad de productos';

-- =============================================================================
-- SEMILLAS DE DATOS (DATA SEEDING)
-- =============================================================================
-- 1. Definimos variables de usuario para usar en todo el script
SET @admin_uuid = UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440000');
SET @vendedor_uuid = UUID_TO_BIN('660e8400-e29b-41d4-a716-446655441111');

-- 1. USUARIOS
INSERT INTO users (id, username, password, email, first_name, last_name, dni, role, is_active, created_at)
VALUES
    (@admin_uuid, 'admin', '$2a$10$7Z2v6f.WlR3y3p5Z.v6f.Ou7U1k1k1k1k1k1k1k1k1k1k1k1k1k1k', 'admin@trade.com', 'Admin', 'Sistemas', '76819073', 'ADMIN',  1, NOW()),
    (@vendedor_uuid, 'vendedor1', '$2a$10$7Z2v6f.WlR3y3p5Z.v6f.Ou7U1k1k1k1k1k1k1k1k1k1k1k1k1k1k', 'ventas@trade.com', 'Jennifer', 'Carrasco', '78006442', 'CASHIER',  1, NOW());

-- 2. CATEGORÍAS
INSERT INTO categories (name, description, is_active, created_at)
VALUES
    ('Polos y Camisetas', 'Prendas superiores de algodón y mezclas', 1, NOW()),
    ('Jeans y Pantalones', 'Pantalones de mezclilla y drill', 1, NOW()),
    ('Calzado Deportivo', 'Zapatillas para running', 1, NOW());

-- 3. UNIDADES DE MEDIDA
INSERT INTO unit_measures (name, abbreviation, allows_decimal)
VALUES
    ('Unidad', 'UND', 0),
    ('Par', 'PR', 0),
    ('Kilogramo', 'KG', 1);

-- 4. PRODUCTOS
SET @cat_polos = (SELECT id FROM categories WHERE name = 'Polos y Camisetas');
SET @cat_jeans = (SELECT id FROM categories WHERE name = 'Jeans y Pantalones');
SET @und = (SELECT id FROM unit_measures WHERE name = 'Unidad');

-- Producto 1
SET @prod_polo_id = UUID_TO_BIN(UUID());
INSERT INTO products (id, sku, barcode, name, description, category_id, unit_measure_id, unit_price, status, created_at, created_by)
VALUES (@prod_polo_id, 'POL-OVR-001', '775123456701', 'Polo Oversize Algodón Pima L', 'Polo negro 100% algodón', @cat_polos, @und, 89.90, 'ACTIVO', NOW(), @admin_uuid);

-- Producto 2
SET @prod_jean_id = UUID_TO_BIN(UUID());
INSERT INTO products (id, sku, barcode, name, description, category_id, unit_measure_id, unit_price, status, created_at, created_by)
VALUES (@prod_jean_id, 'JNS-SLM-042', '775123456702', 'Jean Slim Fit Azul Clásico', 'Jean stretch', @cat_jeans, @und, 149.00, 'ACTIVO', NOW(), @admin_uuid);

-- 5. CLIENTES
INSERT INTO customers (document_type, document_number, full_name, address, phone, email, is_active, created_at)
VALUES ('DNI', '44556677', 'Juan Pérez Delgado', 'Av. Larco 123, Miraflores', '987654321', 'juan.perez@email.com', 1, NOW());

SET @customer_id = (SELECT id FROM customers WHERE document_number = '44556677');

-- 6. INVENTARIO (Corregido: incluyendo created_by y modified_by)
INSERT INTO inventory (product_id, current_stock, min_stock, location, batch_number, is_available, created_at, created_by, modified_by)
VALUES
    (@prod_polo_id, 50.000, 10.000, 'Estante A-1', 'LOTE-2026-001', 1, NOW(), @admin_uuid, @admin_uuid),
    (@prod_jean_id, 30.000, 5.000, 'Almacén Principal', 'LOTE-2026-002', 1, NOW(), @admin_uuid, @admin_uuid);

-- 7. VENTA
SET @sale_id = UUID_TO_BIN(UUID());
INSERT INTO sales (id, user_id, customer_id, subtotal, igv_total, total_amount, payment_method, status, series, number, created_at)
VALUES (@sale_id, @vendedor_uuid, @customer_id, 76.1864, 13.7136, 89.90, 'YAPE', 'COMPLETED', 'B001', 1, NOW());

-- 8. DETALLE DE VENTA
INSERT INTO sale_items (sale_id, product_id, quantity, unit_price, discount_amount, total_item)
VALUES (@sale_id, @prod_polo_id, 1.000, 89.9000, 0.0000, 89.9000);