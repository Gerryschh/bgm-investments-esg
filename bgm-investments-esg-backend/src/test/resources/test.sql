------------------------------------------------------------
-- TEST SQL COMPLETI PER IL DATABASE BGM
-- Contiene 30 query di test (lettura + update + FK errori)
------------------------------------------------------------

-------------------------
-- 1) Utenti
-------------------------
SELECT * FROM utenti;

-------------------------
-- 2) Titoli attivi
-------------------------
SELECT *
FROM titoli
WHERE active = TRUE;

-------------------------
-- 3) Titoli ESG >= 80 ordinati per rendimento
-------------------------
SELECT ticker, nome, settore, esg, rendimento_mensile
FROM titoli
WHERE esg >= 80 AND active = TRUE
ORDER BY rendimento_mensile DESC;

-------------------------
-- 4) Portafogli dell’utente Mario Rossi
-------------------------
SELECT p.id, p.nome, p.created_at
FROM portafogli p
JOIN utenti u ON p.owner_id = u.id
WHERE u.nome = 'Mario Rossi';

-------------------------
-- 5) Posizioni dettagliate nel Portafoglio Dinamico
-------------------------
SELECT p.nome AS portafoglio,
       t.ticker,
       t.nome AS titolo,
       pos.quantita,
       t.prezzo,
       (pos.quantita * t.prezzo) AS controvalore
FROM posizioni pos
JOIN portafogli p ON pos.portfolio_id = p.id
JOIN titoli t ON pos.asset_id = t.id
WHERE p.nome = 'Portafoglio Dinamico';

-------------------------
-- 6) Valore totale del Portafoglio Prudente
-------------------------
SELECT p.nome AS portafoglio,
       SUM(pos.quantita * t.prezzo) AS valore_totale
FROM posizioni pos
JOIN portafogli p ON pos.portfolio_id = p.id
JOIN titoli t ON pos.asset_id = t.id
WHERE p.nome = 'Portafoglio Prudente'
GROUP BY p.nome;

-------------------------
-- 7) Valore totale per ciascun portafoglio
-------------------------
SELECT p.id,
       p.nome AS portafoglio,
       SUM(pos.quantita * t.prezzo) AS valore_totale
FROM portafogli p
LEFT JOIN posizioni pos ON pos.portfolio_id = p.id
LEFT JOIN titoli t ON pos.asset_id = t.id
GROUP BY p.id, p.nome
ORDER BY valore_totale DESC NULLS LAST;

-------------------------
-- 8) Peso percentuale dei titoli nel Portafoglio Bilanciato
-------------------------
WITH tot AS (
    SELECT SUM(pos.quantita * t.prezzo) AS valore_totale
    FROM posizioni pos
    JOIN portafogli p ON pos.portfolio_id = p.id
    JOIN titoli t ON pos.asset_id = t.id
    WHERE p.nome = 'Portafoglio Bilanciato'
)
SELECT t.ticker,
       t.nome AS titolo,
       pos.quantita,
       (pos.quantita * t.prezzo) AS controvalore,
       (pos.quantita * t.prezzo) / tot.valore_totale AS peso_percentuale
FROM posizioni pos
JOIN portafogli p ON pos.portfolio_id = p.id
JOIN titoli t ON pos.asset_id = t.id
CROSS JOIN tot
WHERE p.nome = 'Portafoglio Bilanciato';

-------------------------
-- 9) ESG medio per portafoglio
-------------------------
SELECT p.id,
       p.nome,
       AVG(t.esg) AS esg_medio
FROM portafogli p
JOIN posizioni pos ON pos.portfolio_id = p.id
JOIN titoli t ON pos.asset_id = t.id
GROUP BY p.id, p.nome
ORDER BY esg_medio DESC;

-------------------------
-- 10) Top 5 titoli attivi per volatilità
-------------------------
SELECT ticker, nome, volatilita_mensile
FROM titoli
WHERE active = TRUE
ORDER BY volatilita_mensile DESC
LIMIT 5;

-------------------------
-- 11) Numero titoli per settore
-------------------------
SELECT settore,
       COUNT(*) AS numero_titoli,
       AVG(esg) AS esg_medio_settore
FROM titoli
GROUP BY settore
ORDER BY numero_titoli DESC;

-------------------------
-- 12) Simulazioni a 12 mesi
-------------------------
SELECT *
FROM simulazioni
WHERE mesi = 12;

-------------------------
-- 13) Ultima simulazione per ogni portafoglio
-------------------------
SELECT s.*
FROM simulazioni s
JOIN (
    SELECT portfolio_id, MAX(created_at) AS max_created
    FROM simulazioni
    GROUP BY portfolio_id
) last_sim ON s.portfolio_id = last_sim.portfolio_id
          AND s.created_at = last_sim.max_created;

-------------------------
-- 14) Rapporto rendimento/volatilità
-------------------------
SELECT s.id,
       p.nome AS portafoglio,
       s.mesi,
       s.rendimento_atteso,
       s.volatilita,
       (s.rendimento_atteso / NULLIF(s.volatilita, 0)) AS ratio_rischio_rendimento
FROM simulazioni s
JOIN portafogli p ON s.portfolio_id = p.id
ORDER BY ratio_rischio_rendimento DESC NULLS LAST;

-------------------------
-- 15) Simulazioni con scenario pessimistico < 1 (perdita)
-------------------------
SELECT s.id,
       p.nome AS portafoglio,
       s.mesi,
       s.scenario_neutro,
       s.scenario_ottimistico,
       s.scenario_pessimistico
FROM simulazioni s
JOIN portafogli p ON s.portfolio_id = p.id
WHERE s.scenario_pessimistico < 1.0;

-------------------------
-- 16) Titoli non utilizzati in nessun portafoglio
-------------------------
SELECT t.*
FROM titoli t
LEFT JOIN posizioni pos ON pos.asset_id = t.id
WHERE pos.id IS NULL;

-------------------------
-- 17) Portafogli senza posizioni
-------------------------
SELECT p.*
FROM portafogli p
LEFT JOIN posizioni pos ON pos.portfolio_id = p.id
WHERE pos.id IS NULL;

-------------------------
-- 18a) Aggiornamento prezzo titolo SOLR
-------------------------
UPDATE titoli
SET prezzo = prezzo * 1.05, updated_at = CURRENT_TIMESTAMP
WHERE ticker = 'SOLR';

-------------------------
-- 18b) Verifica aggiornamento
-------------------------
SELECT ticker, nome, prezzo, updated_at
FROM titoli
WHERE ticker = 'SOLR';

-------------------------
-- 19a) Inattiva titoli con ESG < 50
-------------------------
UPDATE titoli
SET active = FALSE
WHERE esg < 50;

-------------------------
-- 19b) Conteggio titoli inattivi
-------------------------
SELECT COUNT(*) AS titoli_inattivi
FROM titoli
WHERE active = FALSE;

-------------------------
-- 20) Inserimento titolo valido
-------------------------
INSERT INTO titoli (ticker, nome, settore, prezzo, esg, rendimento_mensile, volatilita_mensile, source, updated_at, active)
VALUES ('NEW1', 'Nuovo Titolo Test', 'Test', 10.00, 70, 0.010000, 0.030000, 'MANUAL', CURRENT_TIMESTAMP, TRUE);

-------------------------
-- 21) ERRORE ATTESO: ticker duplicato
-------------------------
INSERT INTO titoli (ticker, nome, settore, prezzo, esg, rendimento_mensile, volatilita_mensile, source, updated_at, active)
VALUES ('BGM01', 'Green Bond Italia DUP', 'Bond', 100.00, 80, 0.005000, 0.010000, 'MANUAL', CURRENT_TIMESTAMP, TRUE);

-------------------------
-- 22) ERRORE ATTESO: portafoglio con owner inesistente
-------------------------
INSERT INTO portafogli (nome, owner_id, created_at)
VALUES ('Portafoglio Owner Non Esistente', 999, CURRENT_TIMESTAMP);

-------------------------
-- 23) ERRORE ATTESO: posizione con asset inesistente
-------------------------
INSERT INTO posizioni (portfolio_id, asset_id, quantita)
VALUES (1, 999, 100.0000);

-------------------------
-- 24) ERRORE ATTESO: eliminare titolo referenziato
-------------------------
DELETE FROM titoli
WHERE ticker = 'BGM01';

-------------------------
-- 25a) Rimozione posizioni del Portafoglio Sperimentale
-------------------------
DELETE FROM posizioni
WHERE portfolio_id = (SELECT id FROM portafogli WHERE nome = 'Portafoglio Sperimentale');

-------------------------
-- 25b) Cancellazione del portafoglio Sperimentale (ora OK)
-------------------------
DELETE FROM portafogli
WHERE nome = 'Portafoglio Sperimentale';

-------------------------
-- 26) Titoli aggiornati nelle ultime 24 ore
-------------------------
SELECT *
FROM titoli
WHERE updated_at >= (CURRENT_TIMESTAMP - INTERVAL '1' DAY);

-------------------------
-- 27) Paginazione titoli (primi 5)
-------------------------
SELECT *
FROM titoli
ORDER BY id
LIMIT 5 OFFSET 0;

-------------------------
-- 28) ESG medio titoli attivi per portafoglio
-------------------------
SELECT p.id,
       p.nome,
       AVG(t.esg) AS esg_medio_titoli_attivi
FROM portafogli p
JOIN posizioni pos ON pos.portfolio_id = p.id
JOIN titoli t ON pos.asset_id = t.id
WHERE t.active = TRUE
GROUP BY p.id, p.nome
ORDER BY esg_medio_titoli_attivi DESC;

-------------------------
-- 29) Valore attivo vs valore totale nel Portafoglio Dinamico
-------------------------
WITH valori AS (
    SELECT p.id AS portfolio_id,
           SUM(pos.quantita * t.prezzo) AS valore_totale,
           SUM(CASE WHEN t.active = TRUE THEN pos.quantita * t.prezzo ELSE 0 END) AS valore_attivo
    FROM portafogli p
    JOIN posizioni pos ON pos.portfolio_id = p.id
    JOIN titoli t ON pos.asset_id = t.id
    WHERE p.nome = 'Portafoglio Dinamico'
    GROUP BY p.id
)
SELECT *,
       (valore_attivo / valore_totale) AS percentuale_attivo
FROM valori;

-------------------------
-- 30) Simulazioni con ESG simulato > 85
-------------------------
SELECT s.id,
       p.nome AS portafoglio,
       s.mesi,
       s.esg,
       s.rendimento_atteso,
       s.volatilita
FROM simulazioni s
JOIN portafogli p ON s.portfolio_id = p.id
WHERE s.esg > 85
ORDER BY s.esg DESC, s.mesi;