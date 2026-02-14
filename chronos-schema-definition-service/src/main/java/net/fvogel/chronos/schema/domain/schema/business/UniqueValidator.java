package net.fvogel.chronos.schema.domain.schema.business;

import net.fvogel.chronos.schema.config.i18n.I18nConstants;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.EntityPORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UniqueValidator {

    @Autowired
    EntityPORepository entityPORepository;

    public void validate(EntityPO entityPO) {
        List<ValidationError> errors = new ArrayList<>();

        if (hasDuplicateKey(entityPO)) {
            errors.add(new ValidationError("key", "Unique", I18nConstants.Errors.DUPLICATE_KEY));
        }

        List<String> duplicateAttributeKeys = getDuplicateKeys(entityPO.getAttributes().stream().map(EntityAttributePO::getKey).toList());
        if (!duplicateAttributeKeys.isEmpty()) {
            for (String duplicateKey : duplicateAttributeKeys) {
                List<ValidationError> duplicatesList = entityPO.getAttributes().stream()
                        .filter(attr -> duplicateKey.equals(attr.getKey()))
                        .map(attr -> {
                            int i = entityPO.getAttributes().indexOf(attr);
                            return new ValidationError("attributes[" + i + "].key", "Unique", I18nConstants.Errors.DUPLICATE_KEY);
                        })
                        .toList();
                errors.addAll(duplicatesList);
            }
        }

        List<String> duplicateRelationKeys = getDuplicateKeys(entityPO.getRelations().stream().map(RelationPO::getKey).toList());
        if (!duplicateRelationKeys.isEmpty()) {
            for (String duplicateKey : duplicateRelationKeys) {
                List<ValidationError> duplicatesList = entityPO.getRelations().stream()
                        .filter(rel -> duplicateKey.equals(rel.getKey()))
                        .map(relations -> {
                            int i = entityPO.getRelations().indexOf(relations);
                            return new ValidationError("relations[" + i + "].key", "Unique", I18nConstants.Errors.DUPLICATE_KEY);
                        })
                        .toList();
                errors.addAll(duplicatesList);
            }
        }

        for (RelationPO relationPO : entityPO.getRelations()) {
            try {
                validate(relationPO);
            } catch (ValidationException ve) {
                int i = entityPO.getRelations().indexOf(relationPO);
                errors.addAll(
                        ve.getErrors().stream()
                                .map(err -> new ValidationError("relations[" + i + "]." + err.getField(), err.getConstraint(), err.getMessage()))
                                .toList()
                );
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

    }

    public void validate(RelationPO relationPO) {
        List<ValidationError> errors = new ArrayList<>();

        List<String> duplicateAttributeKeys = getDuplicateKeys(relationPO.getAttributes().stream().map(RelationAttributePO::getKey).toList());
        if (!duplicateAttributeKeys.isEmpty()) {
            for (String duplicateKey : duplicateAttributeKeys) {
                List<ValidationError> duplicatesList = relationPO.getAttributes().stream()
                        .filter(attr -> duplicateKey.equals(attr.getKey()))
                        .map(attr -> {
                            int i = relationPO.getAttributes().indexOf(attr);
                            return new ValidationError("attributes[" + i + "].key", "Unique", I18nConstants.Errors.DUPLICATE_KEY);
                        })
                        .toList();
                errors.addAll(duplicatesList);
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private boolean hasDuplicateKey(EntityPO entityPO) {
        return entityPORepository.findByKey(entityPO.getKey())
                .filter(po -> !Objects.equals(po.getId(), entityPO.getId()))
                .isPresent();
    }

    private List<String> getDuplicateKeys(List<String> keys) {
        List<String> duplicates = new ArrayList<>();
        Set<String> checkedKeys = new HashSet<>();
        for (String key : keys) {
            if (checkedKeys.contains(key)) {
                duplicates.add(key);
            } else {
                checkedKeys.add(key);
            }
        }
        return duplicates;
    }

}
