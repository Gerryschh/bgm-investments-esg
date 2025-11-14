DROP TABLE IF EXISTS posizioni;
DROP TABLE IF EXISTS simulazioni;
DROP TABLE IF EXISTS portafogli;
DROP TABLE IF EXISTS titoli;
DROP TABLE IF EXISTS utenti;

CREATE TABLE utenti
(
    id IDENTITY PRIMARY KEY,
    nome          VARCHAR(100),
    email         VARCHAR(150) UNIQUE,
    password_hash VARCHAR(255),
    ruolo         VARCHAR(20)
);

CREATE TABLE titoli
(
    id IDENTITY PRIMARY KEY,
    ticker             VARCHAR(20) UNIQUE,
    nome               VARCHAR(150),
    settore            VARCHAR(80),
    prezzo             DECIMAL(18, 4),
    esg                INT,
    rendimento_mensile DECIMAL(8, 6),
    volatilita_mensile DECIMAL(8, 6),
    source             VARCHAR(20),
    updated_at         TIMESTAMP,
    active             BOOLEAN
);

CREATE TABLE portafogli
(
    id IDENTITY PRIMARY KEY,
    nome       VARCHAR(120),
    owner_id   BIGINT,
    created_at TIMESTAMP,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES utenti (id)
);

CREATE TABLE posizioni
(
    id IDENTITY PRIMARY KEY,
    portfolio_id BIGINT,
    asset_id     BIGINT,
    quantita     DECIMAL(18, 4),
    CONSTRAINT fk_p FOREIGN KEY (portfolio_id) REFERENCES portafogli (id),
    CONSTRAINT fk_a FOREIGN KEY (asset_id) REFERENCES titoli (id)
);

CREATE TABLE simulazioni
(
    id IDENTITY PRIMARY KEY,
    portfolio_id          BIGINT,
    mesi                  INT,
    esg                   DECIMAL(8, 4),
    rendimento_atteso     DECIMAL(8, 6),
    volatilita            DECIMAL(8, 6),
    scenario_neutro       DECIMAL(10, 6),
    scenario_ottimistico  DECIMAL(10, 6),
    scenario_pessimistico DECIMAL(10, 6),
    created_at            TIMESTAMP,
    CONSTRAINT fk_sp FOREIGN KEY (portfolio_id) REFERENCES portafogli (id)
);