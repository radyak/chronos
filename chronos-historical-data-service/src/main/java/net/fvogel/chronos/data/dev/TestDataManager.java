package net.fvogel.chronos.data.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Profile({"test", "test-data"})
@Component
public class TestDataManager {

    private static final Logger logger = LoggerFactory.getLogger(TestDataManager.class);

    @Autowired
    private Neo4jClient neo4jClient;

    @EventListener(ApplicationReadyEvent.class)
    public void importTestData() throws InterruptedException, IOException {
        waitForDatabase();

        long count = getDatabaseCount();

        if (count == 0) {
            run("/testdata/testdata.cql");
            logger.info("Successfully imported test data");
        } else {
            logger.info("Database already contained {} entries, no test data imported", count);
        }

    }

    public void clearAll() throws IOException {
        run("/testdata/cleanup.cql");
    }

    public long getDatabaseCount() {
        return neo4jClient.query("MATCH (n) RETURN count(n) AS c")
                .fetchAs(Long.class)
                .one()
                .orElse(0L);
    }

    private void run(String script) throws IOException {
        String cypher = new String(
                getClass().getResourceAsStream(script).readAllBytes()
        );
        for (String statement : cypher.split(";")) {
            String query = statement.trim();
            if (!query.isEmpty()) {
                neo4jClient.query(query).run();
            }
        }
    }

    private void waitForDatabase() throws InterruptedException {
        int maxAttempts = 30;
        Duration delay = Duration.ofSeconds(2);

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                neo4jClient.query("RETURN 1").run();
                return;
            } catch (Exception ex) {
                if (attempt == maxAttempts) {
                    throw new IllegalStateException("Neo4j not ready after retries", ex);
                }

                Thread.sleep(delay.toMillis());
            }
        }
    }
}
