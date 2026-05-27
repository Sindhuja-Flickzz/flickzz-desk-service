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

-- Bulk insert into FD_STATE_MASTER
INSERT INTO FD_STATE_MASTER 
    (STATE_NAME, STATE_CODE, COUNTRY_ID, IS_ACTIVE, CREATED_BY, UPDATED_BY)
VALUES
    ('Tamil Nadu', 'TN', 1, TRUE, 'system', 'system'),
    ('California', 'CA', 2, TRUE, 'system', 'system'),
    ('Texas', 'TX', 2, TRUE, 'system', 'system'),
    ('England', 'ENG', 3, TRUE, 'system', 'system'),
    ('Bavaria', 'BY', 4, TRUE, 'system', 'system'),
    ('Île-de-France', 'IDF', 5, TRUE, 'system', 'system'),
    ('Tokyo Prefecture', 'TKY', 6, TRUE, 'system', 'system'),
    ('Guangdong', 'GD', 7, TRUE, 'system', 'system'),
    ('New South Wales', 'NSW', 8, TRUE, 'system', 'system'),
    ('Ontario', 'ON', 9, TRUE, 'system', 'system'),
    ('São Paulo State', 'SP', 10, TRUE, 'system', 'system'),
    ('Moscow Oblast', 'MOS', 11, TRUE, 'system', 'system'),
    ('Gauteng', 'GT', 12, TRUE, 'system', 'system'),
    ('Central Region', 'CR', 13, TRUE, 'system', 'system'),
    ('Dubai Emirate', 'DXB', 14, TRUE, 'system', 'system'),
    ('Lazio', 'LAZ', 15, TRUE, 'system', 'system');

-- Bulk insert into FD_CITY_MASTER
INSERT INTO FD_CITY_MASTER 
    (CITY_NAME, CITY_CODE, STATE_ID, COUNTRY_ID, TIMEZONE, IS_ACTIVE, CREATED_BY, UPDATED_BY)
VALUES
    ('Chennai', 'CHE', 1, 1, 'Asia/Kolkata', TRUE, 'system', 'system'),
    ('Los Angeles', 'LA', 2, 2, 'America/Los_Angeles', TRUE, 'system', 'system'),
    ('Houston', 'HOU', 3, 2, 'America/Chicago', TRUE, 'system', 'system'),
    ('London', 'LDN', 4, 3, 'Europe/London', TRUE, 'system', 'system'),
    ('Munich', 'MUC', 5, 4, 'Europe/Berlin', TRUE, 'system', 'system'),
    ('Paris', 'PAR', 6, 5, 'Europe/Paris', TRUE, 'system', 'system'),
    ('Tokyo', 'TYO', 7, 6, 'Asia/Tokyo', TRUE, 'system', 'system'),
    ('Guangzhou', 'CAN', 8, 7, 'Asia/Shanghai', TRUE, 'system', 'system'),
    ('Sydney', 'SYD', 9, 8, 'Australia/Sydney', TRUE, 'system', 'system'),
    ('Toronto', 'TOR', 10, 9, 'America/Toronto', TRUE, 'system', 'system'),
    ('São Paulo', 'SAO', 11, 10, 'America/Sao_Paulo', TRUE, 'system', 'system'),
    ('Moscow', 'MOW', 12, 11, 'Europe/Moscow', TRUE, 'system', 'system'),
    ('Johannesburg', 'JNB', 13, 12, 'Africa/Johannesburg', TRUE, 'system', 'system'),
    ('Singapore', 'SIN', 14, 13, 'Asia/Singapore', TRUE, 'system', 'system'),
    ('Dubai', 'DXB', 15, 14, 'Asia/Dubai', TRUE, 'system', 'system'),
    ('Rome', 'ROM', 16, 15, 'Europe/Rome', TRUE, 'system', 'system');

-- Bulk insert for FD_LANGUAGE_MASTER
INSERT INTO FD_LANGUAGE_MASTER (LANGUAGE_CODE, LANGUAGE_NAME)
VALUES 
('EN', 'English'),
('FR', 'French'),
('ES', 'Spanish'),
('DE', 'German'),
('IT', 'Italian'),
('PT', 'Portuguese'),
('RU', 'Russian'),
('ZH', 'Chinese'),
('JA', 'Japanese'),
('KO', 'Korean'),
('AR', 'Arabic'),
('HI', 'Hindi'),
('BN', 'Bengali'),
('UR', 'Urdu'),
('FA', 'Persian'),
('TR', 'Turkish'),
('NL', 'Dutch'),
('PL', 'Polish'),
('SV', 'Swedish'),
('NO', 'Norwegian'),
('FI', 'Finnish'),
('DA', 'Danish'),
('EL', 'Greek'),
('HE', 'Hebrew'),
('TH', 'Thai'),
('VI', 'Vietnamese'),
('MS', 'Malay'),
('ID', 'Indonesian'),
('TA', 'Tamil'),
('TE', 'Telugu'),
('KN', 'Kannada'),
('ML', 'Malayalam'),
('MR', 'Marathi'),
('GU', 'Gujarati'),
('PA', 'Punjabi');

INSERT INTO FD_PROGRESS_STATUS 
    (COMPANY_ID, PROGRESS_NAME, PROGRESS_SEQUENCE, COLOR_CODE, IS_ACTIVE, CREATED_BY, UPDATED_BY)
VALUES
    (4, 'NOT STARTED', 1, '#FF0000', TRUE, 'system', 'system');
    
INSERT INTO FD_PROGRESS_STATUS 
    (COMPANY_ID, PROGRESS_NAME, PROGRESS_SEQUENCE, COLOR_CODE, IS_ACTIVE, CREATED_BY, UPDATED_BY)
VALUES
    (4, 'PRIORITIZED', 2, '#FFFF00', TRUE, 'system', 'system');

INSERT INTO FD_PROGRESS_STATUS 
    (COMPANY_ID, PROGRESS_NAME, PROGRESS_SEQUENCE, COLOR_CODE, IS_ACTIVE, CREATED_BY, UPDATED_BY)
VALUES
    (4, 'IN PROGRESS', 3, '#FFA500', TRUE, 'system', 'system');

INSERT INTO FD_PROGRESS_STATUS 
    (COMPANY_ID, PROGRESS_NAME, PROGRESS_SEQUENCE, COLOR_CODE, IS_ACTIVE, CREATED_BY, UPDATED_BY)
VALUES
    (4, 'DONE', 4, '#008000', TRUE, 'system', 'system');

-- Bulk insert for FD_WORK_ITEMS
INSERT INTO FD_WORK_ITEMS (CODE, LABEL, CREATED_BY) VALUES
('TASK', 'Task', 'ADMIN'),
('SUBTASK', 'Subtask', 'ADMIN'),
('EPIC', 'Epic', 'ADMIN'),
('STORY', 'Story', 'ADMIN');

-- Bulk insert for FD_FIELD_TYPES
INSERT INTO FD_FIELD_TYPES (CODE, LABEL, CREATED_BY) VALUES
('TEXTAREA', 'Text Area', 'ADMIN'),
('DROPDOWN', 'Dropdown', 'ADMIN'),
('CHECKBOX', 'Checkbox', 'ADMIN'),
('MULTISELECT', 'Multi-Select Dropdown', 'ADMIN')

