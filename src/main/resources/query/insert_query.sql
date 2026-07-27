-- Bulk Insert into FD_COUNTRY_MASTER

INSERT INTO FD_COUNTRY_MASTER (
    COUNTRY_NAME, ISO_CODE, PHONE_CODE, CURRENCY_CODE, CURRENCY_NAME, TIMEZONE,
    IS_ACTIVE, CREATED_BY, UPDATED_BY, IS_CREATOR_ADMIN
) VALUES
('India', 'IN', '+91', 'INR', 'Indian Rupee', 'Asia/Kolkata', TRUE, 1, 1, TRUE),
('United States', 'US', '+1', 'USD', 'US Dollar', 'America/New_York', TRUE,  1, 1, TRUE),
('United Kingdom', 'GB', '+44', 'GBP', 'British Pound', 'Europe/London', TRUE,  1, 1, TRUE),
('Germany', 'DE', '+49', 'EUR', 'Euro', 'Europe/Berlin', TRUE,  1, 1, TRUE),
('France', 'FR', '+33', 'EUR', 'Euro', 'Europe/Paris', TRUE,  1, 1, TRUE),
('Japan', 'JP', '+81', 'JPY', 'Japanese Yen', 'Asia/Tokyo', TRUE,  1, 1, TRUE),
('China', 'CN', '+86', 'CNY', 'Yuan Renminbi', 'Asia/Shanghai', TRUE,  1, 1, TRUE),
('Australia', 'AU', '+61', 'AUD', 'Australian Dollar', 'Australia/Sydney', TRUE,  1, 1, TRUE),
('Canada', 'CA', '+1', 'CAD', 'Canadian Dollar', 'America/Toronto', TRUE,  1, 1, TRUE),
('Brazil', 'BR', '+55', 'BRL', 'Brazilian Real', 'America/Sao_Paulo', TRUE,  1, 1, TRUE),
('Russia', 'RU', '+7', 'RUB', 'Russian Ruble', 'Europe/Moscow', TRUE,  1, 1, TRUE),
('South Africa', 'ZA', '+27', 'ZAR', 'South African Rand', 'Africa/Johannesburg', TRUE,  1, 1, TRUE),
('Singapore', 'SG', '+65', 'SGD', 'Singapore Dollar', 'Asia/Singapore', TRUE,  1, 1, TRUE),
('United Arab Emirates', 'AE', '+971', 'AED', 'Dirham', 'Asia/Dubai', TRUE,  1, 1, TRUE),
('Italy', 'IT', '+39', 'EUR', 'Euro', 'Europe/Rome', TRUE,  1, 1, TRUE);

-- Bulk insert into FD_STATE_MASTER
INSERT INTO FD_STATE_MASTER 
    (STATE_NAME, STATE_CODE, COUNTRY_ID, IS_ACTIVE, CREATED_BY, UPDATED_BY, IS_CREATOR_ADMIN)
VALUES
    ('Tamil Nadu', 'TN', 1, TRUE,  1, 1, TRUE),
    ('California', 'CA', 2, TRUE,  1, 1, TRUE),
    ('Texas', 'TX', 2, TRUE,  1, 1, TRUE),
    ('England', 'ENG', 3, TRUE,  1, 1, TRUE),
    ('Bavaria', 'BY', 4, TRUE,  1, 1, TRUE),
    ('Île-de-France', 'IDF', 5, TRUE,  1, 1, TRUE),
    ('Tokyo Prefecture', 'TKY', 6, TRUE,  1, 1, TRUE),
    ('Guangdong', 'GD', 7, TRUE,  1, 1, TRUE),
    ('New South Wales', 'NSW', 8, TRUE,  1, 1, TRUE),
    ('Ontario', 'ON', 9, TRUE,  1, 1, TRUE),
    ('São Paulo State', 'SP', 10, TRUE,  1, 1, TRUE),
    ('Moscow Oblast', 'MOS', 11, TRUE,  1, 1, TRUE),
    ('Gauteng', 'GT', 12, TRUE,  1, 1, TRUE),
    ('Central Region', 'CR', 13, TRUE,  1, 1, TRUE),
    ('Dubai Emirate', 'DXB', 14, TRUE,  1, 1, TRUE),
    ('Lazio', 'LAZ', 15, TRUE,  1, 1, TRUE);

-- Bulk insert into FD_CITY_MASTER
INSERT INTO FD_CITY_MASTER 
    (CITY_NAME, CITY_CODE, STATE_ID, COUNTRY_ID, TIMEZONE, IS_ACTIVE, CREATED_BY, UPDATED_BY, IS_CREATOR_ADMIN)
VALUES
    ('Chennai', 'CHE', 1, 1, 'Asia/Kolkata', TRUE,  1, 1, TRUE),
    ('Los Angeles', 'LA', 2, 2, 'America/Los_Angeles', TRUE,  1, 1, TRUE),
    ('Houston', 'HOU', 3, 2, 'America/Chicago', TRUE,  1, 1, TRUE),
    ('London', 'LDN', 4, 3, 'Europe/London', TRUE,  1, 1, TRUE),
    ('Munich', 'MUC', 5, 4, 'Europe/Berlin', TRUE,  1, 1, TRUE),
    ('Paris', 'PAR', 6, 5, 'Europe/Paris', TRUE,  1, 1, TRUE),
    ('Tokyo', 'TYO', 7, 6, 'Asia/Tokyo', TRUE,  1, 1, TRUE),
    ('Guangzhou', 'CAN', 8, 7, 'Asia/Shanghai', TRUE,  1, 1, TRUE),
    ('Sydney', 'SYD', 9, 8, 'Australia/Sydney', TRUE,  1, 1, TRUE),
    ('Toronto', 'TOR', 10, 9, 'America/Toronto', TRUE,  1, 1, TRUE),
    ('São Paulo', 'SAO', 11, 10, 'America/Sao_Paulo', TRUE,  1, 1, TRUE),
    ('Moscow', 'MOW', 12, 11, 'Europe/Moscow', TRUE,  1, 1, TRUE),
    ('Johannesburg', 'JNB', 13, 12, 'Africa/Johannesburg', TRUE,  1, 1, TRUE),
    ('Singapore', 'SIN', 14, 13, 'Asia/Singapore', TRUE, 1, 1, TRUE),
    ('Dubai', 'DXB', 15, 14, 'Asia/Dubai', TRUE, 1, 1, TRUE),
    ('Rome', 'ROM', 16, 15, 'Europe/Rome', TRUE, 1, 1, TRUE);

-- Bulk insert for FD_LANGUAGE_MASTER
INSERT INTO FD_LANGUAGE_MASTER (LANGUAGE_CODE, LANGUAGE_NAME, CREATED_BY, UPDATED_BY, IS_CREATOR_ADMIN)
VALUES 
('EN', 'English', 1, 1, TRUE),
('FR', 'French', 1, 1, TRUE),
('ES', 'Spanish', 1, 1, TRUE),
('DE', 'German', 1, 1, TRUE),
('IT', 'Italian', 1, 1, TRUE),
('PT', 'Portuguese', 1, 1, TRUE),
('RU', 'Russian', 1, 1, TRUE),
('ZH', 'Chinese', 1, 1, TRUE),
('JA', 'Japanese', 1, 1, TRUE),
('KO', 'Korean', 1, 1, TRUE),
('AR', 'Arabic', 1, 1, TRUE),
('HI', 'Hindi', 1, 1, TRUE),
('BN', 'Bengali', 1, 1, TRUE),
('UR', 'Urdu', 1, 1, TRUE),
('FA', 'Persian', 1, 1, TRUE),
('TR', 'Turkish', 1, 1, TRUE),
('NL', 'Dutch', 1, 1, TRUE),
('PL', 'Polish', 1, 1, TRUE),
('SV', 'Swedish', 1, 1, TRUE),
('NO', 'Norwegian', 1, 1, TRUE),
('FI', 'Finnish', 1, 1, TRUE),
('DA', 'Danish', 1, 1, TRUE),
('EL', 'Greek', 1, 1, TRUE),
('HE', 'Hebrew', 1, 1, TRUE),
('TH', 'Thai', 1, 1, TRUE),
('VI', 'Vietnamese', 1, 1, TRUE),
('MS', 'Malay', 1, 1, TRUE),
('ID', 'Indonesian', 1, 1, TRUE),
('TA', 'Tamil', 1, 1, TRUE),
('TE', 'Telugu', 1, 1, TRUE),
('KN', 'Kannada', 1, 1, TRUE),
('ML', 'Malayalam', 1, 1, TRUE),
('MR', 'Marathi', 1, 1, TRUE),
('GU', 'Gujarati', 1, 1, TRUE),
('PA', 'Punjabi', 1, 1, TRUE);

-- Bulk insert for FD_WORK_ITEMS
INSERT INTO FD_WORK_ITEMS (CODE, LABEL, CREATED_BY, IS_CREATOR_ADMIN) VALUES
('TASK', 'Task', 1, TRUE),
('SUBTASK', 'Subtask', 1, TRUE),
('EPIC', 'Epic', 1, TRUE),
('STORY', 'Story', 1, TRUE);

-- Bulk insert for FD_FIELD_TYPES
INSERT INTO FD_FIELD_TYPES (CODE, LABEL, CREATED_BY, IS_CREATOR_ADMIN) VALUES
('TEXTAREA', 'TEXTAREA', 1, TRUE),
('DROPDOWN', 'DROPDOWN', 1, TRUE),
('CHECKBOX', 'CHECKBOX', 1, TRUE),
('MULTISELECT', 'MULTISELECT', 1, TRUE);

---------------------------------------- 12/07/2026	----------------------------------------
INSERT INTO FD_TICKET_TYPE_MASTER (TICKET_TYPE_NAME, IS_ACTIVE, CREATED_BY, IS_CREATOR_ADMIN) VALUES
('Incident', TRUE, 1, TRUE),
('Service Request', TRUE, 1, TRUE),
('Change Request', TRUE, 1, TRUE);