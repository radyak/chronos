package net.fvogel.chronosbackend.config.testdata;

import net.fvogel.chronosbackend.commons.model.schema.AttributeType;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityAttributePO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronosbackend.domain.schema.service.SchemaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Profile("test-data")
@Component
public class TestDataImporter {

    @Bean
    public CommandLineRunner demo(SchemaService service) {
        return (args) -> {

            // ENTITIES

            EntityAttributePO stringAttr = entityAttribute("myString", AttributeType.STRING);
            stringAttr.setValuePattern("[a-z]+");

            EntityAttributePO numberAttr = entityAttribute("myNumber", AttributeType.NUMBER);
            numberAttr.setValueRange("[,1000[");

            EntityAttributePO enumAttr = entityAttribute("myEnum", AttributeType.ENUM);
            enumAttr.setAllowedValues(Arrays.asList("val1", "val2", "val3"));

            EntityPO entityPO = entity("MyEntity", stringAttr, numberAttr, enumAttr);

            service.save(entityPO);


            // RELATIONS

            RelationAttributePO relationAttributePO = relationAttribute("myRelationEnum", AttributeType.ENUM);
            relationAttributePO.setAllowedValues(Arrays.asList("val1", "val2", "val3"));

            RelationPO relationPO = relation("MyRelation", relationAttributePO);
            relationPO.setSource(entityPO);
            relationPO.setTarget(entityPO);

            service.save(relationPO);
        };
    }

    private EntityAttributePO entityAttribute(String name, AttributeType type) {
        EntityAttributePO entityAttributePO = new EntityAttributePO();
        entityAttributePO.setLabel("schema.entity.attr." + name);
        entityAttributePO.setKey(name);
        entityAttributePO.setType(type);
        return entityAttributePO;
    }

    private EntityPO entity(String name, EntityAttributePO... attributes) {
        EntityPO entityPO = new EntityPO();
        entityPO.setKey(name);
        entityPO.setLabel("schema.entity." + name);
        entityPO.getAttributes().addAll(Arrays.asList(attributes));
        return entityPO;
    }

    private RelationAttributePO relationAttribute(String name, AttributeType type) {
        RelationAttributePO relationAttributePO = new RelationAttributePO();
        relationAttributePO.setLabel("schema.relation.attr." + name);
        relationAttributePO.setKey(name);
        relationAttributePO.setType(type);
        return relationAttributePO;
    }

    private RelationPO relation(String name, RelationAttributePO... attributes) {
        RelationPO relationPO = new RelationPO();
        relationPO.setKey(name);
        relationPO.setLabel("schema.relation." + name);
        relationPO.getAttributes().addAll(Arrays.asList(attributes));
        return relationPO;
    }
}
