package net.fvogel.chronos.data.service;

import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.data.model.CountResult;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.ListQuery;
import net.fvogel.chronos.data.model.RelationRecord;
import net.fvogel.chronos.data.service.validation.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Domain layer Service for Entries.
 */
@Service
@Transactional
public class DataService {

    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private CypherService cypherService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ValidationService validationService;

    /**
     * Returns a flat list of entries without relations or related entries.
     * Can take criteria for filtering and sorting entries, plus pagination.
     *
     * @param query The list query that my contain entry filters, sorting and pagination params.
     * @return The resulting list.
     */
    public List<Entry> list(ListQuery query) {
        return cypherService.list(query);
    }

    /**
     * Queries for entries and relations between them, matching conditions given by the query.
     * Returns a list of matching {@link RelationRecord}s, that can be assembled to a compact result with deduped
     * entries and relations.
     *
     * @param query The list query that my contain entry filters, sorting and pagination params.
     * @return The resulting list or records.
     */
    public List<RelationRecord> mesh(ListQuery query) {
        return cypherService.mesh(query);
    }

    /**
     * Returns a single entry, identified by key, without relations or related entries.
     *
     * @param key The key of the targeted entry.
     * @return An {@link Optional} of the targeted entry.
     */
    public Optional<Entry> findByKey(String key) {
        return cypherService.findByKey(key);
    }

    public Optional<Entry> findByKeyAndElementId(String key, String elementId) {
        return cypherService.findByKeyAndElementId(key, elementId);
    }

    public List<CountResult> statistics() {
        return cypherService.statistics();
    }

    public Entry create(Entry entry) {
        entry.get_meta().setCreateAuthor(securityService.getUsername());

        validationService.validate(entry);

        return cypherService.create(entry);
    }

    public Entry update(String key, Entry entry) {
        Entry existing = this.findByKeyAndElementId(key, entry.getElementId()).orElseThrow(NotFoundException::new);
        entry.set_meta(existing.get_meta());
        entry.get_meta().update(securityService.getUsername());
        entry.setLabels(existing.getLabels());

        validationService.validate(entry);

        return cypherService.update(key, entry);
    }

    public void deleteByKey(String key) {
        Entry existing = findByKey(key).orElseThrow(NotFoundException::new);
        String label = existing.getLabels().stream().findFirst().orElseThrow(NotFoundException::new);

        cypherService.delete(label, key);
    }

    public boolean isAttributeUnique(String key, Object value, String elementId) {
        return cypherService.isAttributeUnique(key, value, elementId);
    }

}
