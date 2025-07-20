-- Update payment method constraints to support new enum values
-- Run this script to fix the payment method constraint issue

-- First, drop the existing constraint
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_payment_method_check;

-- Add the new constraint with updated enum values
ALTER TABLE orders ADD CONSTRAINT orders_payment_method_check 
    CHECK (payment_method IN ('MTN_MOBILE_MONEY', 'ORANGE_MONEY', 'CASH_ON_DELIVERY', 'POINTS'));

-- Update existing data to use new enum values
UPDATE orders SET payment_method = 'MTN_MOBILE_MONEY' WHERE payment_method = 'MOBILE_MONEY';

-- Also update payments table if it exists
ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_payment_method_check;
ALTER TABLE payments ADD CONSTRAINT payments_payment_method_check 
    CHECK (payment_method IN ('MTN_MOBILE_MONEY', 'ORANGE_MONEY', 'CASH_ON_DELIVERY', 'POINTS', 'BANK_TRANSFER'));

-- Update existing payment data
UPDATE payments SET payment_method = 'MTN_MOBILE_MONEY' WHERE payment_method = 'MOBILE_MONEY';

-- Verify the changes
SELECT DISTINCT payment_method FROM orders;
SELECT DISTINCT payment_method FROM payments;
