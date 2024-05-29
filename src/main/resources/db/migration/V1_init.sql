CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Таблица торговцев
CREATE TABLE IF NOT EXISTS merchants (
    merchant_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    secret_key VARCHAR(256) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE'))
    -- Дополнительные комментарии и ограничения
);

-- Таблица счетов
CREATE TABLE IF NOT EXISTS accounts (
    account_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    merchant_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL CHECK (balance >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_merchant
        FOREIGN KEY (merchant_id)
        REFERENCES merchants(merchant_id)
        ON DELETE CASCADE
);



-- Таблица клиентов
CREATE TABLE IF NOT EXISTS customers (
    customer_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    country VARCHAR(2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


-- Таблица карт
CREATE TABLE IF NOT EXISTS cards (
    card_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL,
    number VARCHAR(19) NOT NULL,
    expiry_date DATE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(customer_id)
        ON DELETE CASCADE
);
-- Таблица транзакций
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    transaction_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    CONSTRAINT fk_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(account_id)
        ON DELETE CASCADE
);

-- Таблица вебхуков
CREATE TABLE IF NOT EXISTS webhooks (
    webhook_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    transaction_id UUID NOT NULL,
    url VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('SENT', 'FAILED')),
    attempt_count INT NOT NULL DEFAULT 0 CHECK (attempt_count >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES transactions(transaction_id)
        ON DELETE CASCADE
);

-- Индексы для улучшения производительности
CREATE INDEX idx_merchant_status ON merchants(status);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_webhooks_status ON webhooks(status);

-- Триггеры для автоматического обновления поля updated_at
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_merchants_modtime
    BEFORE UPDATE ON merchants
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();

-- Аналогичные триггеры для других таблиц...





_________________________________________________________________________________________________________






CREATE TABLE IF NOT EXISTS merchants (
    merchant_id VARCHAR(255) PRIMARY KEY,
    secret_key VARCHAR(256) NOT NULL,
    constraint merchant_merchant_wallet_id_fk
    references merchant_wallet,
    meta_data JSONB,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(256) NOT NULL,
    updated_by VARCHAR(256),
    status VARCHAR(256) NOT NULL
);


CREATE TABLE IF NOT EXISTS merchant_wallet (
    id integer VARCHAR(256) not null
    constraint merchant_wallet_pk
    primary key,
    amount   numeric,
    currency text
);
CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    merchant_id VARCHAR(255),
    currency VARCHAR(55) NOT NULL,
    amount NUMERIC(18, 2) NOT NULL,
    meta_data JSONB,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(256) NOT NULL,
    updated_by VARCHAR(256),
    status VARCHAR(256) NOT NULL,
    CONSTRAINT accounts_merchant_id_fk FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id)
);



CREATE TABLE IF NOT EXISTS customers (
    card_number VARCHAR(16) PRIMARY KEY,
    first_name VARCHAR(256) NOT NULL,
    last_name VARCHAR(256) NOT NULL,
    country VARCHAR(256) NOT NULL,
    meta_data JSONB,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(256) NOT NULL,
    updated_by VARCHAR(256),
    status VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS cards (
    card_number VARCHAR(16) PRIMARY KEY,
    exp_date DATE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    meta_data JSONB,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(256) NOT NULL,
    updated_by VARCHAR(256),
    status VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id UUID PRIMARY KEY,
    payment_method VARCHAR(25) NOT NULL,
    amount NUMERIC(18, 2) NOT NULL,
    currency VARCHAR(25) NOT NULL,
    language VARCHAR(25) NOT NULL,
    notification_url VARCHAR(256) NOT NULL,
    card_number VARCHAR(16) NOT NULL,
    account_id BIGINT NOT NULL,
    transaction_type VARCHAR(25) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(256) NOT NULL,
    updated_by VARCHAR(256),
    status VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS webhooks (
    id SERIAL PRIMARY KEY,
    transaction_id UUID NOT NULL,
    transaction_attempt BIGINT NOT NULL,
    url_request VARCHAR(256) NOT NULL,
    request_body NOT NULL,
    message VARCHAR(256),
    response_body VARCHAR(256),
    response_status VARCHAR(256), -- 200, 500
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(256) NOT NULL,
    updated_by VARCHAR(256),
    status VARCHAR(256) NOT NULL
);