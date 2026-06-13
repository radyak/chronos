package net.fvogel.chronos.data.service;

import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.data.model.CountResult;
import net.fvogel.chronos.data.model.DataQuery;
import net.fvogel.chronos.data.model.Entry;
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

    public List<Entry> findAll(DataQuery query) {
        return cypherService.findAll(query);
    }

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

}
