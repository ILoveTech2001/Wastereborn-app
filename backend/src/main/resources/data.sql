-- Insert sample admin user
INSERT INTO users (first_name, last_name, email, phone_number, password, role, address, street, city, points_balance, total_orders, total_pickups, recycling_percentage, created_at, updated_at, enabled) 
VALUES 
('Admin', 'User', 'admin@wastereborn.com', '123456789', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'Admin Office', 'Central Street', 'Yaoundé', 0, 0, 0, 0.0, NOW(), NOW(), true),
('John', 'Doe', 'john@example.com', '987654321', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', 'Bastos Quarter', 'Bastos Street', 'Yaoundé', 150, 5, 3, 30.0, NOW(), NOW(), true),
('Jane', 'Smith', 'jane@example.com', '555666777', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', 'Melen Quarter', 'Melen Avenue', 'Yaoundé', 200, 8, 5, 50.0, NOW(), NOW(), true)
ON CONFLICT (email) DO NOTHING;

-- Insert sample products
INSERT INTO products (name, description, category, price, points_price, image_url, stock_quantity, is_available, is_points_redeemable, created_at, updated_at) 
VALUES 
('Recycled Tote Bag', 'Eco-friendly tote bag made from recycled plastic bottles. Perfect for shopping and daily use.', 'Bags', 2000.0, 100, 'https://example.com/tote-bag.jpg', 50, true, true, NOW(), NOW()),
('Eco-friendly Lamp', 'Beautiful desk lamp made from recycled materials. Energy-efficient LED lighting.', 'Electronics', 3500.0, 175, 'https://example.com/eco-lamp.jpg', 25, true, true, NOW(), NOW()),
('Recycled Plastic Chair', 'Durable outdoor chair made from 100% recycled plastic. Weather resistant.', 'Furniture', 5000.0, 250, 'https://example.com/plastic-chair.jpg', 30, true, true, NOW(), NOW()),
('Used Books Set', 'Collection of educational books in good condition. Perfect for students.', 'Books', 1000.0, 50, 'https://example.com/books-set.jpg', 100, true, true, NOW(), NOW()),
('Vintage Shoes', 'Restored vintage shoes in excellent condition. Unique style and comfort.', 'Shoes', 2500.0, 125, 'https://example.com/vintage-shoes.jpg', 20, true, true, NOW(), NOW()),
('Recycled Utensil Set', 'Complete kitchen utensil set made from recycled stainless steel.', 'Utensils', 3000.0, 150, 'https://example.com/utensil-set.jpg', 40, true, true, NOW(), NOW()),
('Bamboo Water Bottle', 'Sustainable water bottle made from bamboo fiber. BPA-free and eco-friendly.', 'Bottles', 1500.0, 75, 'https://example.com/bamboo-bottle.jpg', 60, true, true, NOW(), NOW()),
('Recycled Paper Notebook', 'High-quality notebook made from 100% recycled paper. Perfect for students and professionals.', 'Stationery', 800.0, 40, 'https://example.com/paper-notebook.jpg', 80, true, true, NOW(), NOW()),
('Solar Phone Charger', 'Portable solar charger for smartphones. Environmentally friendly charging solution.', 'Electronics', 4000.0, 200, 'https://example.com/solar-charger.jpg', 15, true, true, NOW(), NOW()),
('Organic Cotton T-Shirt', 'Comfortable t-shirt made from organic cotton. Available in multiple colors.', 'Clothing', 1800.0, 90, 'https://example.com/cotton-tshirt.jpg', 70, true, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Insert sample pickup requests
INSERT INTO pickup_requests (user_id, pickup_address, pickup_street, pickup_city, waste_type, estimated_weight, special_instructions, preferred_pickup_date, status, points_awarded, created_at, updated_at) 
VALUES 
(2, 'Bastos Quarter, House 123', 'Bastos Street', 'Yaoundé', 'Mixed Recyclables', 15.5, 'Please call before arrival', NOW() + INTERVAL '2 days', 'PENDING', 0, NOW(), NOW()),
(3, 'Melen Quarter, Apartment 45', 'Melen Avenue', 'Yaoundé', 'Plastic Waste', 8.2, 'Bags are ready at the gate', NOW() + INTERVAL '3 days', 'SCHEDULED', 0, NOW(), NOW()),
(2, 'Bastos Quarter, House 123', 'Bastos Street', 'Yaoundé', 'Paper Waste', 12.0, 'Large cardboard boxes included', NOW() - INTERVAL '1 week', 'COMPLETED', 5, NOW() - INTERVAL '1 week', NOW() - INTERVAL '1 week'),
(3, 'Melen Quarter, Apartment 45', 'Melen Avenue', 'Yaoundé', 'Electronic Waste', 5.5, 'Old computer and phone', NOW() - INTERVAL '2 weeks', 'COMPLETED', 5, NOW() - INTERVAL '2 weeks', NOW() - INTERVAL '2 weeks')
ON CONFLICT DO NOTHING;

-- Insert sample orders
INSERT INTO orders (user_id, order_number, total_amount, delivery_fee, delivery_address, delivery_city, delivery_phone, status, payment_method, payment_status, estimated_delivery_date, created_at, updated_at) 
VALUES 
(2, 'WR' || EXTRACT(EPOCH FROM NOW())::bigint, 5500.0, 1000.0, 'Bastos Quarter, House 123', 'Yaoundé', '987654321', 'DELIVERED', 'MTN_MOBILE_MONEY', 'PAID', NOW() + INTERVAL '3 days', NOW() - INTERVAL '1 week', NOW() - INTERVAL '1 week'),
(3, 'WR' || (EXTRACT(EPOCH FROM NOW())::bigint + 1), 3800.0, 1000.0, 'Melen Quarter, Apartment 45', 'Yaoundé', '555666777', 'SHIPPED', 'ORANGE_MONEY', 'PAID', NOW() + INTERVAL '2 days', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),
(2, 'WR' || (EXTRACT(EPOCH FROM NOW())::bigint + 2), 2000.0, 1000.0, 'Bastos Quarter, House 123', 'Yaoundé', '987654321', 'PENDING', 'MTN_MOBILE_MONEY', 'PENDING', NOW() + INTERVAL '5 days', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Insert sample order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) 
VALUES 
(1, 1, 1, 2000.0),
(1, 3, 1, 5000.0),
(2, 2, 1, 3500.0),
(2, 6, 1, 3000.0),
(3, 1, 1, 2000.0)
ON CONFLICT DO NOTHING;
