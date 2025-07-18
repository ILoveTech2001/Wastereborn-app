-- Create admin user for WasteReborn
-- Password: 'password' (encoded with BCrypt)

INSERT INTO users (
    first_name, 
    last_name, 
    email, 
    password, 
    role, 
    enabled, 
    points_balance, 
    is_premium,
    registration_date,
    created_at, 
    updated_at
) VALUES (
    'Admin',
    'User',
    'admin@wastereborn.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- BCrypt hash for 'password'
    'ADMIN',
    true,
    0,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;

-- Verify the admin user was created
SELECT id, first_name, last_name, email, role, enabled FROM users WHERE email = 'admin@wastereborn.com';
