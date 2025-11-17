package it.bgm.investments.repo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class TestSqlRepository {

    private final JdbcTemplate jdbcTemplate;

    public TestSqlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Legge test.sql da src/test/resources/test.sql,
     * rimuove i commenti (-- ...) e splitta in singole statement.
     */
    public List<String> loadAllQueries() {
        List<String> queries = new ArrayList<>();

        ClassPathResource resource = new ClassPathResource("test.sql");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder current = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                // ignora commenti e righe vuote
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }

                current.append(trimmed).append("\n");

                if (trimmed.endsWith(";")) {
                    String stmt = current.toString().trim();
                    // toglie l'ultimo ';'
                    int semi = stmt.lastIndexOf(';');
                    if (semi >= 0) {
                        stmt = stmt.substring(0, semi);
                    }
                    queries.add(stmt.trim());
                    current.setLength(0);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Errore nel caricamento di test.sql", e);
        }

        return queries;
    }

    /**
     * Esegue una query singola.
     * - Se inizia con SELECT o WITH → queryForList (ritorna List<Map<String,Object>>)
     * - Altrimenti → update (ritorna Integer)
     * - In caso di errore SQL → ritorna direttamente l'eccezione (non viene rilanciata)
     */
    public Object executeQuery(String query) {
        try {
            String normalized = query.trim().toUpperCase(Locale.ROOT);

            boolean isSelectLike = normalized.startsWith("SELECT") || normalized.startsWith("WITH");

            if (isSelectLike) {
                return jdbcTemplate.queryForList(query);
            } else {
                int updated = jdbcTemplate.update(query);
                return Integer.valueOf(updated);
            }

        } catch (Exception ex) {
            return ex;
        }
    }
}