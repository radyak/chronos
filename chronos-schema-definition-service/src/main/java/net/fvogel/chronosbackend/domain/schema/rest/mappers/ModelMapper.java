package net.fvogel.chronosbackend.domain.schema.rest.mappers;

import net.fvogel.chronosbackend.commons.model.schema.Attribute;
import net.fvogel.chronosbackend.commons.model.schema.Entity;
import net.fvogel.chronosbackend.commons.model.schema.Relation;
import net.fvogel.chronosbackend.commons.model.schema.SchemaResponse;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityAttributePO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronosbackend.domain.schema.persistence.model.relation.RelationAttributePO;

import java.util.stream.Collectors;

public class ModelMapper {

    public Entity toDto(EntityPO entityPO) {
        Entity dto = new Entity();
        dto.setId(entityPO.getId());
        dto.setKey(entityPO.getKey());
        dto.setLabel(entityPO.getLabel());
        dto.setExamples(entityPO.getExamples());
        dto.setExplanation(entityPO.getExplanation());
        dto.setAttributes(entityPO.getAttributes().stream().map(this::toDto).toList());
        return dto;
    }

    public Attribute toDto(EntityAttributePO attribute) {
        Attribute dto = new Attribute();
        dto.setId(attribute.getId());
        dto.setKey(attribute.getKey());
        dto.setLabel(attribute.getLabel());
        dto.setExamples(attribute.getExamples());
        dto.setExplanation(attribute.getExplanation());
        dto.setIsArray(attribute.getIsArray());
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
        dto.setLabel(relationPO.getLabel());
        dto.setExamples(relationPO.getExamples());
        dto.setExplanation(relationPO.getExplanation());
        dto.setAttributes(relationPO.getAttributes().stream().map(this::toDto).toList());
        dto.setSourceEntityId(relationPO.getSource().getId());
        dto.setTargetEntityId(relationPO.getTarget().getId());
        return dto;
    }

    public Attribute toDto(RelationAttributePO attribute) {
        Attribute dto = new Attribute();
        dto.setId(attribute.getId());
        dto.setKey(attribute.getKey());
        dto.setLabel(attribute.getLabel());
        dto.setExamples(attribute.getExamples());
        dto.setExplanation(attribute.getExplanation());
        dto.setIsArray(attribute.getIsArray());
        dto.setType(attribute.getType());
        dto.setAllowedValues(attribute.getAllowedValues());
        dto.setValuePattern(attribute.getValuePattern());
        dto.setValueRange(attribute.getValueRange());
        return dto;
    }

    public void extractToResponseDto(EntityPO entityPO, SchemaResponse responseDTO) {
        responseDTO.getEntities().add(this.toDto(entityPO));
        responseDTO.getRelations().addAll(entityPO.getRelationPOS().stream()
                .map(this::toDto)
                .collect(Collectors.toSet()));
        responseDTO.getEntities().addAll(entityPO.getRelationPOS().stream()
                .map(RelationPO::getTarget)
                .map(this::toDto)
                .collect(Collectors.toSet()));
    }


}
