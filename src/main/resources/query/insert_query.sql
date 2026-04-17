-- Bulk Insert into FD_COUNTRY_MASTER

INSERT INTO FD_COUNTRY_MASTER (
    COUNTRY_NAME, ISO_CODE, PHONE_CODE, CURRENCY_CODE, CURRENCY_NAME, TIMEZONE,
    IS_ACTIVE, CREATED_BY, UPDATED_BY
) VALUES
('India', 'IN', '+91', 'INR', 'Indian Rupee', 'Asia/Kolkata', TRUE, 'system', 'system'),
('United States', 'US', '+1', 'USD', 'US Dollar', 'America/New_York', TRUE, 'system', 'system'),
('United Kingdom', 'GB', '+44', 'GBP', 'British Pound', 'Europe/London', TRUE, 'system', 'system'),
('Germany', 'DE', '+49', 'EUR', 'Euro', 'Europe/Berlin', TRUE, 'system', 'system'),
('France', 'FR', '+33', 'EUR', 'Euro', 'Europe/Paris', TRUE, 'system', 'system'),
('Japan', 'JP', '+81', 'JPY', 'Japanese Yen', 'Asia/Tokyo', TRUE, 'system', 'system'),
('China', 'CN', '+86', 'CNY', 'Yuan Renminbi', 'Asia/Shanghai', TRUE, 'system', 'system'),
('Australia', 'AU', '+61', 'AUD', 'Australian Dollar', 'Australia/Sydney', TRUE, 'system', 'system'),
('Canada', 'CA', '+1', 'CAD', 'Canadian Dollar', 'America/Toronto', TRUE, 'system', 'system'),
('Brazil', 'BR', '+55', 'BRL', 'Brazilian Real', 'America/Sao_Paulo', TRUE, 'system', 'system'),
('Russia', 'RU', '+7', 'RUB', 'Russian Ruble', 'Europe/Moscow', TRUE, 'system', 'system'),
('South Africa', 'ZA', '+27', 'ZAR', 'South African Rand', 'Africa/Johannesburg', TRUE, 'system', 'system'),
('Singapore', 'SG', '+65', 'SGD', 'Singapore Dollar', 'Asia/Singapore', TRUE, 'system', 'system'),
('United Arab Emirates', 'AE', '+971', 'AED', 'Dirham', 'Asia/Dubai', TRUE, 'system', 'system'),
('Italy', 'IT', '+39', 'EUR', 'Euro', 'Europe/Rome', TRUE, 'system', 'system');
