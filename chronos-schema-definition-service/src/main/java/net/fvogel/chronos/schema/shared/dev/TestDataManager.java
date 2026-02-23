package net.fvogel.chronos.schema.shared.dev;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.service.SchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Profile({"test", "test-data"})
@Component
public class TestDataManager {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(TestDataManager.class);

    @Autowired
    SchemaService schemaService;

    public void importTestData() throws IOException {
        this.importTestData("testdata/test-schema.json");
    }

    public void importTestData(String resourcePath) throws IOException {
        long existingEntriesCount = schemaService.entityCount();
        if (existingEntriesCount > 0) {
            logger.info("Database already contains {} entities - no test data will be inserted", existingEntriesCount);
            return;
        }
        List<EntityPO> entityPOs = readEntities(resourcePath);

        logger.info("Database empty - inserting {} entities", entityPOs.size());

        // CREATE ENTITIES FIRST, COLLECT RELATIONS FOR SUBSEQUENT, SEPARATE CREATION
        List<RelationPO> relationPOs = new ArrayList<>();
        entityPOs.forEach(entityPO -> {
            relationPOs.addAll(entityPO.getRelations());
            entityPO.getRelations().clear();
            this.schemaService.save(entityPO);
        });

        // NOW CREATE RELATIONS
        relationPOs.forEach(relationPO -> {
            relationPO.setTarget(findByKey(relationPO.getTarget().getKey(), entityPOs));
            relationPO.setSource(findByKey(relationPO.getSource().getKey(), entityPOs));
            schemaService.save(relationPO);
        });

    }

    public void clearTestData() throws IOException {
        this.clearTestData("testdata/test-schema.json");
    }

    public void clearAll() {
        this.schemaService.allEntities().forEach(entityPO -> schemaService.delete(entityPO.getKey()));
    }

    public void clearTestData(String resourcePath) throws IOException {
        this.readEntities(resourcePath).forEach(entityPO -> schemaService.delete(entityPO.getKey()));
    }

    private List<EntityPO> readEntities(String resourcePath) throws IOException {

        // READ & DESERIALIZE JSON
        InputStream is = TestDataManager.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (is == null) {
            throw new IllegalStateException("Resource not found");
        }

        return mapper.readValue(is, new TypeReference<List<EntityPO>>() {
        });

    }

    private EntityPO findByKey(String key, List<EntityPO> entityPOs) {
        return entityPOs.stream()
                .filter(entityPO -> key.equals(entityPO.getKey()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No EntityPO saved with key '" + key + "'"));
    }

}
