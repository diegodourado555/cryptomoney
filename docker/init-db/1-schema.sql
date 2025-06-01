CREATE TABLE "user"
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email    VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE wallet
(
    id           SERIAL PRIMARY KEY,
    external_id  VARCHAR(100) UNIQUE,
    user_id      INTEGER NOT NULL UNIQUE,
    total        NUMERIC(20, 8) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

CREATE TABLE asset
(
    id           SERIAL PRIMARY KEY,
    wallet_id    INTEGER        NOT NULL,
    symbol VARCHAR(20)    NOT NULL,
    quantity     NUMERIC(20, 8) NOT NULL,
    price        NUMERIC(20, 8) NOT NULL,
    value        NUMERIC(20, 8) NOT NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallet (id) ON DELETE CASCADE
);