package net.fvogel.chronos.schema.domain.schema.rest.mappers;

import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.commons.model.schema.Entity;
import net.fvogel.chronos.commons.model.schema.Relation;
import net.fvogel.chronos.commons.model.schema.SchemaResponse;
import net.fvogel.chronos.schema.domain.schema.business.DefaultEntityAttributesRule;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ModelMapper {

    @Autowired
    DefaultEntityAttributesRule defaultEntityAttributesRule;

    public Entity toDto(EntityPO entityPO) {
        Entity dto = new Entity();
        dto.setId(entityPO.getId());
        dto.setKey(entityPO.getKey());
        dto.setExamples(entityPO.getExamples());
        dto.setExplanation(entityPO.getExplanation());
        dto.setIcon(entityPO.getIcon());
        dto.setAttributes(entityPO.getAttributes().stream().map(this::toDto).toList());
        return dto;
    }

    public Attribute toDto(EntityAttributePO attribute) {
        Attribute dto = new Attribute();
        dto.setId(attribute.getId());
        dto.setKey(attribute.getKey());
        dto.setExamples(attribute.getExamples());
        dto.setExplanation(attribute.getExplanation());
        dto.setIsArray(attribute.getIsArray());
        dto.setIsMandatory(attribute.getIsMandatory());
        dto.setType(attribute.getType());
        dto.setAllowedValues(attribute.getAllowedValues());
        dto.setValuePattern(attribute.getValuePattern());
        dto.setValueRange(attribute.getValueRange());
        return dto;
    }

    public Relation toDto(RelationPO relationPO) {
        Relation dto = new Relation();
        dto.setId(relationPO.getId());
        dto.setKey(relationPO.getKey());
        dto.setExamples(relationPO.getExamples());
        dto.setExplanation(relationPO.getExplanation());
        dto.setAttributes(relationPO.getAttributes().stream().map(this::toDto).toList());
        if (relationPO.getSource() != null) {
            dto.setSourceEntityId(relationPO.getSource().getId());
        }
        if (relationPO.getTarget() != null) {
            dto.setTargetEntityId(relationPO.getTarget().getId());
        }
        return dto;
    }

    public Attribute toDto(RelationAttributePO attribute) {
        Attribute dto = new Attribute();
        dto.setId(attribute.getId());
        dto.setKey(attribute.getKey());
        dto.setExamples(attribute.getExamples());
        dto.setExplanation(attribute.getExplanation());
        dto.setIsArray(attribute.getIsArray());
        dto.setIsMandatory(attribute.getIsMandatory());
        dto.setType(attribute.getType());
        dto.setAllowedValues(attribute.getAllowedValues());
        dto.setValuePattern(attribute.getValuePattern());
        dto.setValueRange(attribute.getValueRange());
        return dto;
    }

    public void extractToResponseDto(EntityPO entityPO, SchemaResponse responseDTO) {
        responseDTO.getEntities().getElements().add(this.toDto(entityPO));
        responseDTO.getRelations().getElements().addAll(entityPO.getRelations().stream()
                .map(this::toDto)
                .collect(Collectors.toSet()));
        responseDTO.getRelations().getElements().addAll(entityPO.getInboundRelations().stream()
                .map(this::toDto)
                .collect(Collectors.toSet()));
        responseDTO.getEntities().getElements().addAll(entityPO.getRelations().stream()
                .map(RelationPO::getTarget)
                .map(this::toDto)
                .collect(Collectors.toSet()));
        responseDTO.getEntities().getElements().addAll(entityPO.getInboundRelations().stream()
                .map(RelationPO::getSource)
                .map(this::toDto)
                .collect(Collectors.toSet()));

        responseDTO.getEntities().setDefaultAttributes(defaultEntityAttributesRule.getDefaultEntityAttributes());
        responseDTO.getRelations().setDefaultAttributes(defaultEntityAttributesRule.getDefaultRelationAttributes());
    }


}
