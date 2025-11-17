package it.bgm.investments.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class DatabaseQueryPerQueryTest {

    @Autowired
    private TestSqlRepository repo;

    private List<String> allQueries() {
        List<String> queries = repo.loadAllQueries();
        assertEquals(33, queries.size(), "Mi aspetto 33 statement in test.sql");
        return queries;
    }

    private String getQuery(int index1Based) {
        List<String> queries = allQueries();
        assertTrue(index1Based >= 1 && index1Based <= queries.size(),
                "Indice query fuori range: " + index1Based);
        return queries.get(index1Based - 1);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> asRowList(Object res) {
        return (List<Map<String, Object>>) res;
    }

    // 1) SELECT * FROM utenti;
    @Test
    void q01_users_notEmpty() {
        Object res = repo.executeQuery(getQuery(1));
        assertFalse(res instanceof Exception);
        List<?> rows = (List<?>) res;
        assertFalse(rows.isEmpty(), "Dovrebbe esserci almeno un utente");
    }

    // 2) Titoli attivi
    @Test
    void q02_activeAssets_notEmpty_andActiveTrue() {
        Object res = repo.executeQuery(getQuery(2));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        assertFalse(rows.isEmpty(), "Dovrebbe esserci almeno un titolo attivo");
        rows.forEach(r -> assertEquals(Boolean.TRUE, r.get("ACTIVE")));
    }

    // 3) Titoli ESG >= 80
    @Test
    void q03_highEsgAssets_esgAtLeast80() {
        Object res = repo.executeQuery(getQuery(3));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        rows.forEach(r -> {
            Number esg = (Number) r.get("ESG");
            assertTrue(esg.intValue() >= 80);
        });
    }

    // 4) Portafogli Mario Rossi
    @Test
    void q04_marioRossiPortfolios_notEmpty() {
        Object res = repo.executeQuery(getQuery(4));
        assertFalse(res instanceof Exception);
        List<?> rows = (List<?>) res;
        assertFalse(rows.isEmpty(), "Mario Rossi dovrebbe avere almeno un portafoglio");
    }

    // 5) Posizioni Portafoglio Dinamico
    @Test
    void q05_dynamicPortfolioPositions_countervalueNonNegative() {
        Object res = repo.executeQuery(getQuery(5));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        rows.forEach(r -> {
            Number cv = (Number) r.get("CONTROVALORE");
            assertNotNull(cv);
            assertTrue(cv.doubleValue() >= 0.0);
        });
    }

    // 6) Valore totale Portafoglio Prudente
    @Test
    void q06_prudentPortfolio_totalValuePositive() {
        Object res = repo.executeQuery(getQuery(6));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        assertEquals(1, rows.size());
        Number value = (Number) rows.get(0).get("VALORE_TOTALE");
        assertNotNull(value);
        assertTrue(value.doubleValue() > 0.0);
    }

    // 7) Valore totale per ciascun portafoglio
    @Test
    void q07_totalValueByPortfolio_notEmpty() {
        Object res = repo.executeQuery(getQuery(7));
        assertFalse(res instanceof Exception);
        List<?> rows = (List<?>) res;
        assertFalse(rows.isEmpty());
    }

    // 8) Peso percentuale nel Portafoglio Bilanciato
    @Test
    void q08_balancedPortfolio_weightsSumApproxOne() {
        Object res = repo.executeQuery(getQuery(8));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        double sum = rows.stream()
                .map(r -> (Number) r.get("PESO_PERCENTUALE"))
                .mapToDouble(Number::doubleValue)
                .sum();
        assertTrue(sum > 0.99 && sum < 1.01,
                "Somma pesi ~1, ottenuto: " + sum);
    }

    // 9) ESG medio per portafoglio
    @Test
    void q09_avgEsgByPortfolio_notEmpty() {
        Object res = repo.executeQuery(getQuery(9));
        assertFalse(res instanceof Exception);
        List<?> rows = (List<?>) res;
        assertFalse(rows.isEmpty());
    }

    // 10) Top 5 titoli per volatilità
    @Test
    void q10_top5ByVolatility_max5Rows() {
        Object res = repo.executeQuery(getQuery(10));
        assertFalse(res instanceof Exception);
        List<?> rows = (List<?>) res;
        assertTrue(rows.size() <= 5);
    }

    // 11) Numero titoli per settore
    @Test
    void q11_assetsBySector_notEmpty() {
        Object res = repo.executeQuery(getQuery(11));
        assertFalse(res instanceof Exception);
        List<?> rows = (List<?>) res;
        assertFalse(rows.isEmpty());
    }

    // 12) Simulazioni a 12 mesi
    @Test
    void q12_simulations12Months_allMesi12() {
        Object res = repo.executeQuery(getQuery(12));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        rows.forEach(r -> assertEquals(12, ((Number) r.get("MESI")).intValue()));
    }

    // 13) Ultima simulazione per ciascun portafoglio
    @Test
    void q13_lastSimulationByPortfolio_notEmpty() {
        Object res = repo.executeQuery(getQuery(13));
        assertFalse(res instanceof Exception);
        List<?> rows = (List<?>) res;
        assertFalse(rows.isEmpty());
    }

    // 14) Rapporto rendimento/volatilità
    @Test
    void q14_riskReturnRatio_notNull() {
        Object res = repo.executeQuery(getQuery(14));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        rows.forEach(r -> assertNotNull(r.get("RATIO_RISCHIO_RENDIMENTO")));
    }

    // 15) scenario_pessimistico < 1
    @Test
    void q15_negativeWorstCaseSimulations_pessimisticBelowOne() {
        Object res = repo.executeQuery(getQuery(15));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        rows.forEach(r -> {
            Number s = (Number) r.get("SCENARIO_PESSIMISTICO");
            assertTrue(s.doubleValue() < 1.0);
        });
    }

    // 16) Titoli non utilizzati
    @Test
    void q16_unusedAssets_runs() {
        Object res = repo.executeQuery(getQuery(16));
        assertFalse(res instanceof Exception);
        // 0 o più righe, ok (nel tuo dataset dovrebbe esserci BANK)
    }

    // 17) Portafogli senza posizioni (dovrebbero essere 0 con il tuo data.sql)
    @Test
    void q17_emptyPortfolios_zeroOrMore() {
        Object res = repo.executeQuery(getQuery(17));
        assertFalse(res instanceof Exception);
    }

    // 18) Aggiornamento prezzo SOLR
    @Test
    void q18_updateSolr_affectsAtLeastOneRow() {
        Object res = repo.executeQuery(getQuery(18));
        assertFalse(res instanceof Exception);
        assertTrue(res instanceof Integer);
        assertTrue(((Integer) res) >= 1);
    }

    // 19) Verifica aggiornamento SOLR
    @Test
    void q19_selectSolr_hasSingleRowWithPrice() {
        Object res = repo.executeQuery(getQuery(19));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        assertEquals(1, rows.size());
        assertNotNull(rows.get(0).get("PREZZO"));
    }

    // 20) Inattiva titoli con ESG < 50 (nel tuo dataset saranno 0)
    @Test
    void q20_deactivateLowEsg_zeroOrMoreUpdated() {
        Object res = repo.executeQuery(getQuery(20));
        assertFalse(res instanceof Exception);
        assertTrue(res instanceof Integer);
        assertTrue(((Integer) res) >= 0);
    }

    // 21) Count titoli inattivi
    @Test
    void q21_countInactiveAssets_nonNegative() {
        Object res = repo.executeQuery(getQuery(21));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        assertEquals(1, rows.size());
        Number n = (Number) rows.get(0).get("TITOLI_INATTIVI");
        assertTrue(n.intValue() >= 0);
    }

    // 22) Inserimento titolo valido NEW1
    @Test
    void q22_insertValidAsset_insertsOneRow() {
        Object res = repo.executeQuery(getQuery(22));
        assertFalse(res instanceof Exception);
        assertTrue(res instanceof Integer);
        assertEquals(1, ((Integer) res).intValue());
    }

    // 23) Ticker duplicato → deve fallire
    @Test
    void q23_duplicateTicker_shouldFail() {
        Object res = repo.executeQuery(getQuery(23));
        assertTrue(res instanceof Exception,
                "Query inserirmento ticker duplicato dovrebbe dare errore (unique)");
    }

    // 24) Portafoglio owner inesistente → deve fallire
    @Test
    void q24_invalidOwner_shouldFail() {
        Object res = repo.executeQuery(getQuery(24));
        assertTrue(res instanceof Exception,
                "Portafoglio con owner inesistente dovrebbe dare errore di FK");
    }

    // 25) Posizione con asset inesistente → deve fallire
    @Test
    void q25_invalidAssetPosition_shouldFail() {
        Object res = repo.executeQuery(getQuery(25));
        assertTrue(res instanceof Exception,
                "Posizione con asset inesistente dovrebbe dare errore di FK");
    }

    // 26) Eliminare titolo referenziato → deve fallire
    @Test
    void q26_deleteReferencedAsset_shouldFail() {
        Object res = repo.executeQuery(getQuery(26));
        assertTrue(res instanceof Exception,
                "Eliminare BGM01 (referenziato) dovrebbe dare errore di FK");
    }

    // 27) Delete posizioni Portafoglio Sperimentale (nel dataset non esiste → 0 righe)
    @Test
    void q27_deleteExperimentalPositions_zeroRows() {
        Object res = repo.executeQuery(getQuery(27));
        assertFalse(res instanceof Exception);
        assertTrue(res instanceof Integer);
        // deve essere >= 0, ma nel tuo data.sql è 0
        assertEquals(0, ((Integer) res).intValue());
    }

    // 28) Delete Portafoglio Sperimentale (non esiste → 0 righe)
    @Test
    void q28_deleteExperimentalPortfolio_zeroRows() {
        Object res = repo.executeQuery(getQuery(28));
        assertFalse(res instanceof Exception);
        assertTrue(res instanceof Integer);
        assertEquals(0, ((Integer) res).intValue());
    }

    // 29) Titoli aggiornati ultime 24 ore
    @Test
    void q29_assetsUpdatedLast24h_notEmpty() {
        Object res = repo.executeQuery(getQuery(29));
        assertFalse(res instanceof Exception);
        List<?> rows = (List<?>) res;
        assertFalse(rows.isEmpty());
    }

    // 30) Paginazione titoli (primi 5)
    @Test
    void q30_firstPageAssets_max5Rows() {
        Object res = repo.executeQuery(getQuery(30));
        assertFalse(res instanceof Exception);
        List<?> rows = (List<?>) res;
        assertTrue(rows.size() <= 5);
    }

    // 31) ESG medio titoli attivi per portafoglio
    @Test
    void q31_avgEsgOfActiveAssetsByPortfolio_runs() {
        Object res = repo.executeQuery(getQuery(31));
        assertFalse(res instanceof Exception);
    }

    // 32) Valore attivo vs totale nel Dinamico
    @Test
    void q32_dynamicPortfolioActiveVsTotal_hasPercentuale() {
        Object res = repo.executeQuery(getQuery(32));
        assertFalse(res instanceof Exception);
        List<Map<String, Object>> rows = asRowList(res);
        assertEquals(1, rows.size());
        assertNotNull(rows.get(0).get("PERCENTUALE_ATTIVO"));
    }

    // 33) Simulazioni con ESG simulato > 85
    @Test
    void q33_simulationsHighEsg_runs() {
        Object res = repo.executeQuery(getQuery(33));
        assertFalse(res instanceof Exception);
    }
}
