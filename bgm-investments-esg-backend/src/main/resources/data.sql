-- Utente/admin demo (password = "password")

-------------------------------------------------------------------
-- UTENTI
-------------------------------------------------------------------

INSERT INTO utenti (nome, email, password_hash, ruolo)
VALUES ('Mario Rossi', 'mario.rossi@bgm.it', '$2y$16$Qc5THrRdpP5RLa4g0g5LnuPThaDBGK2cmCwVZTBwZQ/wmUCZoNpy6', 'USER'),
       ('Admin BGM', 'admin@bgm.it', '$2y$16$Qc5THrRdpP5RLa4g0g5LnuPThaDBGK2cmCwVZTBwZQ/wmUCZoNpy6', 'ADMIN');

-------------------------------------------------------------------
-- TITOLI
-------------------------------------------------------------------

INSERT INTO titoli (ticker, nome, settore, prezzo, esg, rendimento_mensile, volatilita_mensile, source, updated_at,active)
VALUES ('BGM01', 'Green Bond Italia 2028', 'Bond', 102.10, 82, 0.004, 0.012, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('SOLR', 'SolarTech SpA', 'Energy', 19.80, 88, 0.018, 0.060, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('WATR', 'AcquaPure', 'Utilities', 12.30, 75, 0.010, 0.030, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('BANK', 'BGM Bank', 'Financial', 7.40, 62, 0.008, 0.045, 'MANUAL', CURRENT_TIMESTAMP, TRUE);
       ('WIND',  'WindPower Europe','Energy', 23.50, 90, 0.020000, 0.065000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('EVIT',  'EV Italia','Automotive', 34.20, 86, 0.022000, 0.080000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('REIT1', 'Green Real Estate REIT', 'RealEstate', 18.75, 78, 0.009000, 0.028000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('FOOD',  'BioFood Corp', 'Consumer', 15.60, 82, 0.012000, 0.035000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('TECH',  'CleanTech Global', 'Technology', 42.10, 89, 0.025000, 0.090000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('HYDR',  'HydroLife Utilities',  'Utilities', 11.90, 80, 0.011000, 0.032000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('BOND2', 'Green Bond Europa 2030', 'Bond', 101.40, 84, 0.003500, 0.011000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('BOND3', 'Social Bond Italia 2029', 'Bond', 100.80, 88, 0.003800, 0.010500, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('MICRO', 'MicroFin Impact Fund', 'Financial', 21.30, 87, 0.015000, 0.055000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('EDU',   'Education Impact Fund', 'Services', 17.40, 90, 0.013000, 0.045000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('HEALTH','HealthyLife Pharma', 'Healthcare', 28.60, 83, 0.014000, 0.050000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('RECY',  'RecycleNow Industries', 'Industrial', 9.80,  81, 0.016000, 0.070000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('AGRI',  'AgriFuture Europe', 'Agriculture', 13.90, 85, 0.012500, 0.052000, 'MANUAL', CURRENT_TIMESTAMP, TRUE),
       ('TRANS', 'Clean Transport ETF', 'ETF', 50.20, 88, 0.019000, 0.075000, 'MANUAL', CURRENT_TIMESTAMP, TRUE);

-------------------------------------------------------------------
-- PORTAFOGLI
-------------------------------------------------------------------

INSERT INTO portafogli (nome, owner_id, created_at)
VALUES
  ('Portafoglio Prudente',        1, CURRENT_TIMESTAMP),
  ('Portafoglio Dinamico',        1, CURRENT_TIMESTAMP),
  ('Portafoglio ESG Alto',        1, CURRENT_TIMESTAMP),
  ('Portafoglio Obbligazionario', 1, CURRENT_TIMESTAMP),
  ('Portafoglio Bilanciato',      1, CURRENT_TIMESTAMP);


-------------------------------------------------------------------
-- POSIZIONI
-------------------------------------------------------------------

-- Portafoglio 1: prudente, molto bond/utilities
INSERT INTO posizioni (portfolio_id, asset_id, quantita)
VALUES
  (1, 1,  150.0000),   -- BGM01
  (1, 3,  220.0000),   -- WATR
  (1, 11, 100.0000),   -- BOND2
  (1, 12, 80.0000);    -- BOND3

-- Portafoglio 2: dinamico, growth/tech/energy
INSERT INTO posizioni (portfolio_id, asset_id, quantita)
VALUES
  (2, 2,  300.0000),   -- SOLR
  (2, 5,  200.0000),   -- WIND
  (2, 6,  120.0000),   -- EVIT
  (2, 9,  90.0000),    -- TECH
  (2, 18, 75.0000),    -- TRANS
  (2, 16, 150.0000);   -- RECY

-- Portafoglio 3: forte focus ESG alto
INSERT INTO posizioni (portfolio_id, asset_id, quantita)
VALUES
  (3, 2,  180.0000),   -- SOLR
  (3, 5,  140.0000),   -- WIND
  (3, 7,  160.0000),   -- REIT1
  (3, 9,  110.0000),   -- TECH
  (3, 14, 130.0000),   -- EDU
  (3, 15, 95.0000),    -- HEALTH
  (3, 17, 210.0000);   -- AGRI

-- Portafoglio 4: obbligazionario (quasi solo bond)
INSERT INTO posizioni (portfolio_id, asset_id, quantita)
VALUES
  (4, 1,  300.0000),   -- BGM01
  (4, 11, 250.0000),   -- BOND2
  (4, 12, 200.0000),   -- BOND3
  (4, 3,  100.0000);   -- WATR (un po' di utilities a basso rischio)

-- Portafoglio 5: bilanciato
INSERT INTO posizioni (portfolio_id, asset_id, quantita)
VALUES
  (5, 1,  120.0000),   -- BGM01
  (5, 8,  160.0000),   -- FOOD
  (5, 10, 140.0000),   -- HYDR
  (5, 13, 90.0000),    -- MICRO
  (5, 15, 80.0000),    -- HEALTH
  (5, 18, 60.0000);    -- TRANS


 -------------------------------------------------------------------
 -- SIMULAZIONI
 -------------------------------------------------------------------
 -- Alcune simulazioni per ciascun portafoglio con orizzonti diversi

 -- Portafoglio 1 (Prudente)
 INSERT INTO simulazioni (portfolio_id, mesi, esg, rendimento_atteso, volatilita,
                          scenario_neutro, scenario_ottimistico, scenario_pessimistico, created_at)
 VALUES
   (1, 12, 78.5000, 0.006500, 0.020000, 1.065000, 1.120000, 0.980000, CURRENT_TIMESTAMP),
   (1, 24, 79.0000, 0.006800, 0.021000, 1.140000, 1.250000, 0.930000, CURRENT_TIMESTAMP);

 -- Portafoglio 2 (Dinamico)
 INSERT INTO simulazioni (portfolio_id, mesi, esg, rendimento_atteso, volatilita,
                          scenario_neutro, scenario_ottimistico, scenario_pessimistico, created_at)
 VALUES
   (2, 12, 84.2000, 0.015000, 0.080000, 1.180000, 1.350000, 0.850000, CURRENT_TIMESTAMP),
   (2, 36, 85.0000, 0.017500, 0.095000, 1.750000, 2.200000, 0.600000, CURRENT_TIMESTAMP);

 -- Portafoglio 3 (ESG Alto)
 INSERT INTO simulazioni (portfolio_id, mesi, esg, rendimento_atteso, volatilita,
                          scenario_neutro, scenario_ottimistico, scenario_pessimistico, created_at)
 VALUES
   (3, 12, 88.7000, 0.013500, 0.060000, 1.160000, 1.280000, 0.900000, CURRENT_TIMESTAMP),
   (3, 24, 89.1000, 0.014000, 0.065000, 1.320000, 1.550000, 0.780000, CURRENT_TIMESTAMP);

 -- Portafoglio 4 (Obbligazionario)
 INSERT INTO simulazioni (portfolio_id, mesi, esg, rendimento_atteso, volatilita,
                          scenario_neutro, scenario_ottimistico, scenario_pessimistico, created_at)
 VALUES
   (4, 12, 80.0000, 0.004200, 0.015000, 1.050000, 1.090000, 0.990000, CURRENT_TIMESTAMP),
   (4, 36, 81.5000, 0.004500, 0.016000, 1.150000, 1.220000, 0.900000, CURRENT_TIMESTAMP);

 -- Portafoglio 5 (Bilanciato)
 INSERT INTO simulazioni (portfolio_id, mesi, esg, rendimento_atteso, volatilita,
                          scenario_neutro, scenario_ottimistico, scenario_pessimistico, created_at)
 VALUES
   (5, 12, 83.0000, 0.009500, 0.045000, 1.110000, 1.220000, 0.940000, CURRENT_TIMESTAMP),
   (5, 24, 83.5000, 0.010200, 0.050000, 1.250000, 1.450000, 0.820000, CURRENT_TIMESTAMP);