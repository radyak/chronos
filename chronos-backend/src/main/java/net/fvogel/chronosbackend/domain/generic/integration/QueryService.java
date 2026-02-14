package net.fvogel.chronosbackend.domain.generic.integration;

import net.fvogel.chronos.commons.exception.InvalidDataException;
import net.fvogel.chronosbackend.domain.generic.integration.query.node.CreateNodeQuery;
import net.fvogel.chronosbackend.domain.generic.integration.query.node.DeleteNodeQuery;
import net.fvogel.chronosbackend.domain.generic.integration.query.node.UpdateNodeQuery;
import net.fvogel.chronosbackend.domain.generic.model.EntityFieldMetadata;
import net.fvogel.chronosbackend.domain.generic.model.EntityMetadata;
import net.fvogel.chronosbackend.domain.generic.persistence.Entity;
import net.fvogel.chronosbackend.domain.generic.service.MetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static net.fvogel.chronosbackend.domain.generic.service.ReflectionUtils.getFieldStringValue;


@Service
@Transactional
public class QueryService {

    private static final Logger logger = LoggerFactory.getLogger(QueryService.class);
    private static final Pattern valuePattern = Pattern.compile("^[a-zA-Z0-9\\s-_]{0,36}$");

    private final MetadataService metadataService;

    public QueryService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public CreateNodeQuery createNodeQuery(String type, Entity entity) {
        if (!this.metadataService.entityExists(type)) {
            throw new InvalidDataException();
        }

        CreateNodeQuery.CreateNodeQueryBuilder qb = new CreateNodeQuery.CreateNodeQueryBuilder();
        return qb.label(type)
                .properties(toMap(type, entity))
                .build();
    }

    public UpdateNodeQuery updateNodeQuery(String type, Entity entity) {
        if (!this.metadataService.entityExists(type)) {
            throw new InvalidDataException();
        }

        UpdateNodeQuery.UpdateNodeQueryBuilder qb = new UpdateNodeQuery.UpdateNodeQueryBuilder();
        qb.label(type);
        qb.id(entity.id);
        qb.properties(toMap(type, entity));
        return qb.build();
    }

    public DeleteNodeQuery deleteNodeQuery(String type, String id) {
        if (!this.metadataService.entityExists(type)) {
            throw new InvalidDataException();
        }

        DeleteNodeQuery.DeleteNodeQueryBuilder qb = new DeleteNodeQuery.DeleteNodeQueryBuilder();
        qb.label(type);
        qb.id(id);
        return qb.build();
    }

    private Map<String, String> toMap(String type, Entity entity) {
        EntityMetadata metadata = this.metadataService.getMetaData(type);
        List<EntityFieldMetadata> fields = metadata.getFields();

        Map<String, String> properties = new HashMap<>();

        for (EntityFieldMetadata field : fields) {
            try {
                String value = getFieldStringValue(entity, field.getName());
                if (value == null) {
                    continue;
                }
                if (!valuePattern.matcher(value).matches()) {
                    throw new InvalidDataException();
                }
                properties.put(field.getName(), value);
            } catch (Exception e) {
                logger.error("Error while mapping field {} of type {}:", field.getName(), type, e);
            }
        }
        return properties;
    }

}
