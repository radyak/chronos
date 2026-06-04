package net.fvogel.chronos.schema.domain.schema.rest.mappers;

import net.fvogel.chronos.commons.model.schema.Attribute;
import net.fvogel.chronos.commons.model.schema.Relation;
import net.fvogel.chronos.commons.model.schema.SchemaResponse;
import net.fvogel.chronos.commons.model.schema.Type;
import net.fvogel.chronos.schema.domain.schema.business.DefaultTypeAttributesRule;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypeAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ModelMapper {

    @Autowired
    DefaultTypeAttributesRule defaultTypeAttributesRule;

    public Type toDto(TypePO typePO) {
        Type dto = new Type();
        dto.setId(typePO.getId());
        dto.setKey(typePO.getKey());
        dto.setExamples(typePO.getExamples());
        dto.setExplanation(typePO.getExplanation());
        dto.setIcon(typePO.getIcon());
        dto.setAttributes(typePO.getAttributes().stream().map(this::toDto).toList());
        return dto;
    }

    public Attribute toDto(TypeAttributePO attribute) {
        Attribute dto = new Attribute();
        dto.setId(attribute.getId());
        dto.setKey(attribute.getKey());
        dto.setExamples(attribute.getExamples());
        dto.setExplanation(attribute.getExplanation());

        dto.setIsMandatory(attribute.getIsMandatory());
        dto.setIsUnique(attribute.getIsUnique());
        dto.setIsChangeable(attribute.getIsChangeable());
        dto.setIsArray(attribute.getIsArray());
        dto.setOrder(attribute.getOrder());

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
            dto.setSourceTypeId(relationPO.getSource().getId());
        }
        if (relationPO.getTarget() != null) {
            dto.setTargetTypeId(relationPO.getTarget().getId());
        }
        return dto;
    }

    public Attribute toDto(RelationAttributePO attribute) {
        Attribute dto = new Attribute();
        dto.setId(attribute.getId());
        dto.setKey(attribute.getKey());
        dto.setExamples(attribute.getExamples());
        dto.setExplanation(attribute.getExplanation());

        dto.setIsMandatory(attribute.getIsMandatory());
        dto.setIsUnique(attribute.getIsUnique());
        dto.setIsChangeable(attribute.getIsChangeable());
        dto.setIsArray(attribute.getIsArray());
        dto.setOrder(attribute.getOrder());

        dto.setType(attribute.getType());
        dto.setAllowedValues(attribute.getAllowedValues());
        dto.setValuePattern(attribute.getValuePattern());
        dto.setValueRange(attribute.getValueRange());
        return dto;
    }

    public void extractToResponseDto(TypePO typePO, SchemaResponse responseDTO) {
        responseDTO.getTypes().getElements().add(this.toDto(typePO));
        responseDTO.getRelations().getElements().addAll(typePO.getRelations().stream()
                .map(this::toDto)
                .collect(Collectors.toSet()));
        responseDTO.getRelations().getElements().addAll(typePO.getInboundRelations().stream()
                .map(this::toDto)
                .collect(Collectors.toSet()));
        responseDTO.getTypes().getElements().addAll(typePO.getRelations().stream()
                .map(RelationPO::getTarget)
                .map(this::toDto)
                .collect(Collectors.toSet()));
        responseDTO.getTypes().getElements().addAll(typePO.getInboundRelations().stream()
                .map(RelationPO::getSource)
                .map(this::toDto)
                .collect(Collectors.toSet()));

        responseDTO.getTypes().setDefaultAttributes(defaultTypeAttributesRule.getDefaultTypeAttributes());
        responseDTO.getRelations().setDefaultAttributes(defaultTypeAttributesRule.getDefaultRelationAttributes());
    }


}
