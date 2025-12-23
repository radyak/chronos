package net.fvogel.chronosbackend.config.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;

@Configuration
@Profile("test-data")
public class TestDataImportConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestDataImportConfig.class);

    @Autowired
    private DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        // TODO: Populate test data
    }

}
