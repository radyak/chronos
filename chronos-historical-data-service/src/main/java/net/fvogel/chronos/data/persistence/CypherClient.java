package net.fvogel.chronos.data.persistence;

import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.cypherdsl.core.renderer.Renderer;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Client for Neo4j Cypher statements.
 * Based on <a href="https://neo4j.github.io/cypher-dsl/2025.2.6/">Neo4j Cypher-DSL</a>
 */
@Service
public class CypherClient {

    private static final Logger logger = LoggerFactory.getLogger(CypherClient.class);

    @Autowired
    private Driver driver;

    public <T> T runStatement(Statement statement, ResultExtractor<T> resultExtractor) {
        var renderedStatement = Renderer.getDefaultRenderer().render(statement);

        logger.debug("Executing statement: {}", renderedStatement);

        try (Session session = driver.session()) {
            Result result = session.run(renderedStatement);
            return resultExtractor.extract(result);
        }
    }

    public void runStatement(Statement statement) {
        var renderedStatement = Renderer.getDefaultRenderer().render(statement);

        logger.debug("Executing statement: {}", renderedStatement);

        try (Session session = driver.session()) {
            Result result = session.run(renderedStatement);
        }
    }
}
