package net.fvogel.chronos.schema.domain.schema.service;

import jakarta.transaction.Transactional;
import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.schema.config.caching.CachingConfig;
import net.fvogel.chronos.schema.domain.schema.business.DefaultTypeAttributesRule;
import net.fvogel.chronos.schema.domain.schema.business.UniqueValidator;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.relation.RelationPO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypeAttributePO;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.RelationAttributePORepository;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.RelationPORepository;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.TypeAttributePORepository;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.TypePORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class SchemaService {

    @Autowired
    TypePORepository typePORepository;
    @Autowired
    TypeAttributePORepository typeAttributePORepository;
    @Autowired
    RelationPORepository relationPORepository;
    @Autowired
    RelationAttributePORepository relationAttributePORepository;
    @Autowired
    DefaultTypeAttributesRule defaultTypeAttributesRule;
    @Autowired
    UniqueValidator uniqueValidator;

    public long typeCount() {
        return this.typePORepository.count();
    }

    @Cacheable({CachingConfig.CacheNames.SCHEMA_CACHE})
    public Set<TypePO> allTypes() {
        return new HashSet<>(this.typePORepository.findAll());
    }

    @Cacheable({CachingConfig.CacheNames.TYPE_CACHE})
    public TypePO getTypeByKey(String key) {
        return this.typePORepository.findByKey(key).orElseThrow(NotFoundException::new);
    }

    public void assertTypeExistsByKey(String key) {
        getTypeByKey(key);
    }

    @CacheEvict(
            value = {
                    CachingConfig.CacheNames.SCHEMA_CACHE,
                    CachingConfig.CacheNames.TYPE_CACHE
            },
            allEntries = true
    )
    public TypePO save(TypePO typePO) {
        uniqueValidator.validate(typePO);

        // Avoid persisting of default attributes
        List<TypeAttributePO> specificAttributes = typePO.getAttributes().stream()
                .filter(attr -> defaultTypeAttributesRule.isDefaultTypeAttribute(attr.getKey()))
                .toList();
        typePO.setAttributes(specificAttributes);

        for (TypeAttributePO attribute : typePO.getAttributes()) {
            this.typeAttributePORepository.save(attribute);
        }

        TypePO result = this.typePORepository.save(typePO);

        for (RelationPO relationPO : typePO.getRelations()) {
            relationPO.setSource(typePO);
            this.save(relationPO);
        }

        return result;
    }

    @CacheEvict(
            value = {
                    CachingConfig.CacheNames.TYPE_CACHE,
                    CachingConfig.CacheNames.SCHEMA_CACHE,
            },
            allEntries = true
    )
    public void delete(String key) {
        TypePO typePO = this.getTypeByKey(key);
        this.typePORepository.delete(typePO);
    }

    public RelationPO save(RelationPO relationPO) {
        uniqueValidator.validate(relationPO);

        // Avoid persisting of default attributes
        List<RelationAttributePO> specificAttributes = relationPO.getAttributes().stream()
                .filter(attr -> defaultTypeAttributesRule.isDefaultRelationAttribute(attr.getKey()))
                .toList();
        relationPO.setAttributes(specificAttributes);

        for (RelationAttributePO attribute : relationPO.getAttributes()) {
            this.relationAttributePORepository.save(attribute);
        }
        return this.relationPORepository.save(relationPO);
    }

}
