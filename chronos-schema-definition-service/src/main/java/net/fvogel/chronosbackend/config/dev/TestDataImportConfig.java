package net.fvogel.chronosbackend.config.dev;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronosbackend.domain.schema.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Profile("test-data")
@Configuration
public class TestDataImportConfig {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    SchemaService service;

    @EventListener(ApplicationReadyEvent.class)
    public void importTestData() throws IOException {
        this.importTestData("testdata/test-schema.json");
    }

    public void importTestData(String resourcePath) throws IOException {

        // READ & DESERIALIZE JSON
        InputStream is = TestDataImportConfig.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (is == null) {
            throw new IllegalStateException("Resource not found");
        }

        List<EntityPO> entityPOs = mapper.readValue(is, new TypeReference<List<EntityPO>>() {
        });

        // CREATE ENTITIES FIRST, COLLECT RELATIONS FOR SUBSEQUENT, SEPARATE CREATION
        List<RelationPO> relationPOs = new ArrayList<>();
        entityPOs.forEach(entityPO -> {
            relationPOs.addAll(entityPO.getRelations());
            entityPO.getRelations().clear();
            this.service.save(entityPO);
        });

        // NOW CREATE RELATIONS
        relationPOs.forEach(relationPO -> {
            relationPO.setTarget(findByKey(relationPO.getTarget().getKey(), entityPOs));
            relationPO.setSource(findByKey(relationPO.getSource().getKey(), entityPOs));
            service.save(relationPO);
        });

    }

    private EntityPO findByKey(String key, List<EntityPO> entityPOs) {
        return entityPOs.stream()
                .filter(entityPO -> key.equals(entityPO.getKey()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No EntityPO saved with key '" + key + "'"));
    }

}
