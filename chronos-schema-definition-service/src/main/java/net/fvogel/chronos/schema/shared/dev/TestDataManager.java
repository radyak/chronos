package net.fvogel.chronos.schema.shared.dev;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
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
        long existingEntriesCount = schemaService.typeCount();
        if (existingEntriesCount > 0) {
            logger.info("Database already contains {} types - no test data will be inserted", existingEntriesCount);
            return;
        }
        List<TypePO> typePOS = readTypes(resourcePath);

        logger.info("Database empty - inserting {} types", typePOS.size());

        // CREATE TYPES FIRST, COLLECT RELATIONS FOR SUBSEQUENT, SEPARATE CREATION
        List<RelationPO> relationPOs = new ArrayList<>();
        typePOS.forEach(typePO -> {
            relationPOs.addAll(typePO.getRelations());
            typePO.getRelations().clear();
            this.schemaService.save(typePO);
        });

        // NOW CREATE RELATIONS
        relationPOs.forEach(relationPO -> {
            relationPO.setTarget(findByKey(relationPO.getTarget().getKey(), typePOS));
            relationPO.setSource(findByKey(relationPO.getSource().getKey(), typePOS));
            schemaService.save(relationPO);
        });

    }

    public void clearTestData() throws IOException {
        this.clearTestData("testdata/test-schema.json");
    }

    public void clearAll() {
        this.schemaService.allTypes().forEach(typePO -> schemaService.delete(typePO.getKey()));
    }

    public void clearTestData(String resourcePath) throws IOException {
        this.readTypes(resourcePath).forEach(typePO -> schemaService.delete(typePO.getKey()));
    }

    private List<TypePO> readTypes(String resourcePath) throws IOException {

        // READ & DESERIALIZE JSON
        InputStream is = TestDataManager.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (is == null) {
            throw new IllegalStateException("Resource not found");
        }

        return mapper.readValue(is, new TypeReference<List<TypePO>>() {
        });

    }

    private TypePO findByKey(String key, List<TypePO> typePOS) {
        return typePOS.stream()
                .filter(typePO -> key.equals(typePO.getKey()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No TypePO saved with key '" + key + "'"));
    }

}
