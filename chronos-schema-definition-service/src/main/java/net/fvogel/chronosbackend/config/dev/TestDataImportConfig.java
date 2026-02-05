package net.fvogel.chronosbackend.config.dev;

import net.fvogel.chronosbackend.shared.dev.TestDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import java.io.IOException;

@Profile("test-data")
@Configuration
public class TestDataImportConfig {

    @Autowired
    TestDataManager testDataManager;

    @EventListener(ApplicationReadyEvent.class)
    public void importTestData() throws IOException {
        this.testDataManager.importTestData();
    }

}
