-- Tạo database
CREATE DATABASE IF NOT EXISTS quanlikho CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quanlikho;

-- Bảng roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Bảng users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng warehouses
CREATE TABLE IF NOT EXISTS warehouses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location VARCHAR(255),
    description TEXT,
    capacity INT
);

-- Bảng user_roles (nhiều-nhiều)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Bảng categories
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Bảng products (đồng bộ lại với entity, thêm code, đảm bảo unique cho code và sku)
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    sku VARCHAR(50) NOT NULL UNIQUE,
    quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(15,2) NOT NULL,
    unit VARCHAR(20),
    category_id BIGINT,
    warehouse_id BIGINT,
    min_quantity INT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) 
);

-- Bảng suppliers
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100)
);

-- Bảng inventory_transactions
DROP TABLE IF EXISTS inventory_transactions;
CREATE TABLE IF NOT EXISTS inventory_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL, -- IMPORT, EXPORT
    quantity INT NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    note TEXT,
    document_number VARCHAR(50) NOT NULL,
    supplier_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    warehouse_id BIGINT,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

-- Bảng zones (mỗi zone thuộc 1 warehouse)
CREATE TABLE IF NOT EXISTS zones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zone_code VARCHAR(50) NOT NULL,
    location VARCHAR(255),
    capacity INT,
    zone_type VARCHAR(50),
    warehouse_id BIGINT,
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

-- Bảng aisles (mỗi aisle thuộc 1 zone)
CREATE TABLE IF NOT EXISTS aisles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aisle_code VARCHAR(50) NOT NULL,
    location VARCHAR(255),
    capacity INT,
    zone_id BIGINT,
    FOREIGN KEY (zone_id) REFERENCES zones(id)
);

-- Bảng racks (mỗi rack thuộc 1 aisle)
CREATE TABLE IF NOT EXISTS racks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rack_code VARCHAR(50) NOT NULL,
    height DOUBLE,
    capacity DOUBLE,
    aisle_id BIGINT,
    FOREIGN KEY (aisle_id) REFERENCES aisles(id)
);

-- Bảng shelves (mỗi shelf thuộc 1 rack)
CREATE TABLE IF NOT EXISTS shelves (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shelf_code VARCHAR(50) NOT NULL,
    max_weight DOUBLE,
    capacity INT,
    rack_id BIGINT,
    FOREIGN KEY (rack_id) REFERENCES racks(id)
);

-- Bảng bins (mỗi bin thuộc 1 shelf)
CREATE TABLE IF NOT EXISTS bins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bin_code VARCHAR(50) NOT NULL,
    max_weight DOUBLE,
    current_weight DOUBLE,
    shelf_id BIGINT,
    FOREIGN KEY (shelf_id) REFERENCES shelves(id)
);

-- Bảng bin_products (nhiều-nhiều giữa bins và products)
DROP TABLE IF EXISTS bin_products;
CREATE TABLE IF NOT EXISTS bin_products (
    bin_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    PRIMARY KEY (bin_id, product_id),
    FOREIGN KEY (bin_id) REFERENCES bins(id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Bảng revenues (mỗi revenue liên kết với 1 product)
DROP TABLE IF EXISTS revenues;
CREATE TABLE IF NOT EXISTS revenues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    product_id BIGINT,
    quantity_sold INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Dữ liệu mẫu roles
INSERT IGNORE INTO roles (name) VALUES ('ADMIN'), ('MANAGER'), ('WAREHOUSE_STAFF');

-- Gán quyền ADMIN cho admin
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Dữ liệu mẫu Categories
INSERT IGNORE INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and gadgets'),
('Clothing', 'Apparel and fashion items'),
('Books', 'Various genres of books'),
('Home Appliances', 'Appliances for home use'),
('Furniture', 'Household and office furniture');

-- Dữ liệu mẫu Suppliers
INSERT IGNORE INTO suppliers (name, address, phone, email) VALUES
('Tech Supply Co.', '123 Tech Street', '090111222', 'contact@techsupply.com'),
('Fashion House', '456 Fashion Ave', '090333444', 'info@fashionhouse.net'),
('Book World', '789 Book Lane', '090555666', 'sales@bookworld.org'),
('Appliance Pros', '101 Appliance Way', '090777888', 'support@appliancepros.biz'),
('Furnishings Inc.', '202 Furniture Blvd', '090999000', 'hello@furnishingsinc.co');

-- Dữ liệu mẫu Warehouses
INSERT IGNORE INTO warehouses (name, location, description, capacity) VALUES
('Main Warehouse A', 'Industrial Zone 1', 'Primary storage facility', 10000),
('Warehouse B', 'City Outskirts', 'Secondary storage', 5000),
('Warehouse C', 'Port Area', 'Import/Export focus', 7500),
('Warehouse D', 'Downtown Storage', 'Small items storage', 3000),
('Warehouse E', 'Northern Region', 'Bulk storage', 12000);

-- Dữ liệu mẫu Zones (liên kết với warehouses 1-5)
INSERT IGNORE INTO zones (zone_code, location, capacity, zone_type, warehouse_id) VALUES
('ZA01', 'Zone A - Section 1', 2000, 'General', 1),
('ZB01', 'Zone B - Section 1', 1000, 'Cold Storage', 2),
('ZC01', 'Zone C - Section 1', 1500, 'Bonded', 3),
('ZD01', 'Zone D - Section 1', 600, 'Picking', 4),
('ZE01', 'Zone E - Section 1', 2400, 'Bulk', 5);

-- Dữ liệu mẫu Aisles (liên kết với zones 1-5)
INSERT IGNORE INTO aisles (aisle_code, location, capacity, zone_id) VALUES
('AA01A', 'Aisle A1', 500, 1),
('AB01A', 'Aisle B1', 200, 2),
('AC01A', 'Aisle C1', 300, 3),
('AD01A', 'Aisle D1', 120, 4),
('AE01A', 'Aisle E1', 480, 5);

-- Dữ liệu mẫu Racks (liên kết với aisles 1-5)
INSERT IGNORE INTO racks (rack_code, height, capacity, aisle_id) VALUES
('RA01A1', 5.0, 100, 1),
('RB01A1', 4.0, 40, 2),
('RC01A1', 6.0, 60, 3),
('RD01A1', 3.0, 24, 4),
('RE01A1', 7.0, 96, 5);

-- Dữ liệu mẫu Shelves (liên kết với racks 1-5)
INSERT IGNORE INTO shelves (shelf_code, max_weight, capacity, rack_id) VALUES
('SA01A1S1', 100.0, 10, 1),
('SB01A1S1', 80.0, 8, 2),
('SC01A1S1', 120.0, 12, 3),
('SD01A1S1', 60.0, 5, 4),
('SE01A1S1', 150.0, 15, 5);

-- Dữ liệu mẫu Bins (liên kết với shelves 1-5)
INSERT IGNORE INTO bins (bin_code, max_weight, current_weight, shelf_id) VALUES
('BA01A1S1B1', 20.0, 0.0, 1),
('BB01A1S1B1', 15.0, 0.0, 2),
('BC01A1S1B1', 25.0, 0.0, 3),
('BD01A1S1B1', 10.0, 0.0, 4),
('BE01A1S1B1', 30.0, 0.0, 5);

-- Dữ liệu mẫu Products (10 sản phẩm, liên kết với categories 1-5 và warehouses 1-5)
INSERT IGNORE INTO products (name, code, description, sku, quantity, price, unit, category_id, min_quantity, warehouse_id) VALUES
('Laptop X1', 'LAPX1', 'High performance laptop', 'SKULAPX1', 50, 1200.00, 'pcs', 1, 10, 1),
('Smartphone Pro', 'SMPRO', 'Latest model smartphone', 'SKUSMPRO', 150, 800.00, 'pcs', 1, 30, 1),
('T-Shirt Cotton', 'TSCOTT', '100% cotton t-shirt', 'SKUTSCOTT', 200, 15.00, 'pcs', 2, 50, 2),
('Jeans Slim Fit', 'JNSLIM', 'Blue slim fit jeans', 'SKUJNSLIM', 100, 45.00, 'pcs', 2, 20, 2),
('The Great Novel', 'GRNOVEL', 'Bestseller novel', 'SKUGRNOVEL', 80, 20.00, 'pcs', 3, 15, 3),
('History Textbook', 'HISTEXT', 'University level history book', 'SKUHISTEXT', 30, 50.00, 'pcs', 3, 5, 3),
('Microwave Oven', 'MWOVN', 'Compact microwave', 'SKUMWOVN', 40, 100.00, 'pcs', 4, 8, 4),
('Blender Power', 'BLENDERP', 'High power blender', 'SKUBLENDERP', 60, 75.00, 'pcs', 4, 12, 4),
('Office Desk', 'OFCDSK', 'Standard office desk', 'SKUOFCDSK', 25, 150.00, 'pcs', 5, 5, 5),
('Chair Ergonomic', 'CHAIRE', 'Ergonomic office chair', 'SKUCHAIRE', 35, 90.00, 'pcs', 5, 7, 5);

-- Dữ liệu mẫu Inventory Transactions (5 giao dịch, liên kết với products, users (admin id = 1), warehouses và suppliers)
-- Giả định user admin có ID = 1
INSERT IGNORE INTO inventory_transactions (product_id, transaction_type, quantity, transaction_date, created_by, note, document_number, supplier_id, warehouse_id) VALUES
(1, 'IMPORT', 10, NOW(), 1, 'Initial stock', 'IMP001', 1, 1),
(3, 'IMPORT', 50, NOW(), 1, 'New shipment', 'IMP002', 2, 2),
(1, 'EXPORT', 2, NOW(), 1, 'Sale to customer A', 'EXP001', NULL, 1),
(5, 'IMPORT', 20, NOW(), 1, 'Restock', 'IMP003', 3, 3),
(7, 'EXPORT', 5, NOW(), 1, 'Sale to customer B', 'EXP002', NULL, 4);

-- Dữ liệu mẫu Bin Products (liên kết một số sản phẩm với bins)
INSERT IGNORE INTO bin_products (bin_id, product_id) VALUES
(1, 1), (1, 2),
(2, 3), (2, 4),
(3, 5), (3, 6),
(4, 7), (4, 8),
(5, 9), (5, 10);

-- Dữ liệu mẫu Revenues (5 bản ghi, liên kết với products)
INSERT IGNORE INTO revenues (date, amount, product_id, quantity_sold) VALUES
('2023-10-26', 2400.00, 1, 2), -- 2 * 1200
('2023-10-26', 675.00, 8, 5),  -- 5 * 135 (giả định giá bán có thể khác giá nhập)
('2023-10-25', 450.00, 4, 10), -- 10 * 45
('2023-10-25', 1000.00, 7, 10), -- 10 * 100
('2023-10-24', 180.00, 10, 2); -- 2 * 90 