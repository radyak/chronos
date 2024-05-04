package net.fvogel.chronosbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;

import javax.sql.DataSource;

@Configuration
@Profile("test-data")
public class TestDataImportConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestDataImportConfig.class);

    @Autowired
    private DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        try {
            ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource("import-test-data.sql"));
            resourceDatabasePopulator.execute(dataSource);
        } catch (ScriptStatementFailedException e) {
            logger.warn("Unable to import test data", e);
        }
    }

}
