package net.fvogel.chronos.data.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.io.IOException;

@Profile("test-data")
@Configuration
public class TestDataImportConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestDataImportConfig.class);

    @Autowired
    private Neo4jClient neo4jClient;

    @EventListener(ApplicationReadyEvent.class)
    public void importTestData() throws IOException {
        run("/testdata/cleanup-relations.cql");
        run("/testdata/cleanup-nodes.cql");
        run("/testdata/testdata.cql");

        logger.info("Successfully imported test data");
    }

    private void run(String script) throws IOException {
        String cypher = new String(
                getClass().getResourceAsStream(script).readAllBytes()
        );

        neo4jClient.query(cypher).run();
    }
}
