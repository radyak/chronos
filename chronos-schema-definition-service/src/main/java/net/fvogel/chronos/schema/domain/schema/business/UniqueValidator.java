package net.fvogel.chronos.schema.domain.schema.business;

import net.fvogel.chronos.schema.config.i18n.I18nConstants;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypeAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.TypePORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UniqueValidator {

    @Autowired
    TypePORepository typePORepository;

    public void validate(TypePO typePO) {
        List<ValidationError> errors = new ArrayList<>();

        if (hasDuplicateKey(typePO)) {
            errors.add(new ValidationError("key", "Unique", I18nConstants.Errors.DUPLICATE_KEY));
        }

        List<String> duplicateAttributeKeys = getDuplicateKeys(typePO.getAttributes().stream().map(TypeAttributePO::getKey).toList());
        if (!duplicateAttributeKeys.isEmpty()) {
            for (String duplicateKey : duplicateAttributeKeys) {
                List<ValidationError> duplicatesList = typePO.getAttributes().stream()
                        .filter(attr -> duplicateKey.equals(attr.getKey()))
                        .map(attr -> {
                            int i = typePO.getAttributes().indexOf(attr);
                            return new ValidationError("attributes[" + i + "].key", "Unique", I18nConstants.Errors.DUPLICATE_KEY);
                        })
                        .toList();
                errors.addAll(duplicatesList);
            }
        }

        List<String> duplicateRelationKeys = getDuplicateKeys(typePO.getRelations().stream().map(RelationPO::getKey).toList());
        if (!duplicateRelationKeys.isEmpty()) {
            for (String duplicateKey : duplicateRelationKeys) {
                List<ValidationError> duplicatesList = typePO.getRelations().stream()
                        .filter(rel -> duplicateKey.equals(rel.getKey()))
                        .map(relations -> {
                            int i = typePO.getRelations().indexOf(relations);
                            return new ValidationError("relations[" + i + "].key", "Unique", I18nConstants.Errors.DUPLICATE_KEY);
                        })
                        .toList();
                errors.addAll(duplicatesList);
            }
        }

        for (RelationPO relationPO : typePO.getRelations()) {
            try {
                validate(relationPO);
            } catch (ValidationException ve) {
                int i = typePO.getRelations().indexOf(relationPO);
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

    private boolean hasDuplicateKey(TypePO typePO) {
        return typePORepository.findByKey(typePO.getKey())
                .filter(po -> !Objects.equals(po.getId(), typePO.getId()))
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
