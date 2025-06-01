-- Insert users
INSERT INTO "user" (username, email)
VALUES ('alice', 'alice@example.com'),
       ('bob', 'bob@example.com'),
       ('carol', 'carol@example.com'),
       ('diego', 'diego@example.com');

-- Insert wallets (one per user)
INSERT INTO wallet (external_id, user_id, total)
VALUES ('abc123', 1, 1000.00),
       ('def456', 2, 500.50),
       ('ghi789', 3, 0.00),
       ('123456', 4, 158000.00);  -- Matches example wallet

-- Insert assets (multiple per wallet)
INSERT INTO asset (wallet_id, symbol, quantity, price, value)
VALUES (1, 'BTC', 0.5, 50000.00, 25000.00),
       (1, 'ETH', 10.0, 3000.00, 30000.00),
       (2, 'BTC', 0.1, 50000.00, 5000.00),
       (2, 'DOGE', 1000.0, 0.50, 500.00),
       (3, 'ETH', 0.0, 3000.00, 0.00),
       -- Assets for the example wallet
       (4, 'BTC', 1.5, 100000.00, 150000.00),
       (4, 'ETH', 2.0, 4000.00, 8000.00);