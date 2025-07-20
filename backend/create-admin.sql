-- Create admin user if not exists
-- This script ensures the admin user has the correct role

-- Insert admin user (password is 'admin123' hashed with BCrypt)
INSERT INTO users (name, email, phone, password, role, created_at, updated_at) 
VALUES (
    'Admin User', 
    'admin@wastereborn.com', 
    '+1234567890', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- BCrypt hash for 'admin123'
    'ADMIN', 
    NOW(), 
    NOW()
) 
ON DUPLICATE KEY UPDATE 
    role = 'ADMIN',
    updated_at = NOW();

-- Verify admin user exists
SELECT id, name, email, role FROM users WHERE email = 'admin@wastereborn.com';
