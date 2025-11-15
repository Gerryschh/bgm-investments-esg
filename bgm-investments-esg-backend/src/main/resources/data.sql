-- Utente/admin demo (password = "password")
INSERT INTO utenti (nome, email, password_hash, ruolo)
VALUES ('Mario Rossi', 'mario.rossi@bgm.it', '$2y$16$Qc5THrRdpP5RLa4g0g5LnuPThaDBGK2cmCwVZTBwZQ/wmUCZoNpy6', 'USER'),
       ('Admin BGM', 'admin@bgm.it', '$2y$16$Qc5THrRdpP5RLa4g0g5LnuPThaDBGK2cmCwVZTBwZQ/wmUCZoNpy6', 'ADMIN');

INSERT INTO titoli (ticker, nome, settore, prezzo, esg, rendimento_mensile, volatilita_mensile, source, updated_at,active)
VALUES ('BGM01', 'Green Bond Italia 2028', 'Bond', 102.10, 82, 0.004, 0.012, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('SOLR', 'SolarTech SpA', 'Energy', 19.80, 88, 0.018, 0.060, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('WATR', 'AcquaPure', 'Utilities', 12.30, 75, 0.010, 0.030, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('BANK', 'BGM Bank', 'Financial', 7.40, 62, 0.008, 0.045, 'MANUAL', CURRENT_TIMESTAMP, TRUE);