package net.fvogel.chronosbackend.common.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import net.fvogel.chronosbackend.common.exception.InvalidParameterException;
import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;
import net.fvogel.chronosbackend.common.persistence.entries.repo.EntryRepository;
import net.fvogel.chronosbackend.wikipedia.model.WikipediaSummary;
import net.fvogel.chronosbackend.wikipedia.service.WikipediaService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntriesService {

    private static final Logger logger = LoggerFactory.getLogger(EntriesService.class);

    private EntryRepository entryRepository;
    private WikipediaService wikipediaService;
    private EntityManager em;

    public EntriesService(EntryRepository entryRepository,
                          WikipediaService wikipediaService,
                          EntityManager em) {
        this.entryRepository = entryRepository;
        this.wikipediaService = wikipediaService;
        this.em = em;
    }

    public Entry save(Entry entry) {
        return this.entryRepository.save(entry);
    }

    public List<Entry> saveAll(Collection<Entry> entries) {
        return this.entryRepository.saveAll(entries);
    }

    public List<Entry> findAll() {
        return this.entryRepository.findAll();
    }

    public List<Entry> find(String title, Set<Long> tagIds, Short from, Short to) {
        if (tagIds != null && tagIds.size() == 0) {
            tagIds = null;
        }
        LocalDate fromDate = (from != null ? LocalDate.ofYearDay(from, 1) : null);
        LocalDate toDate = (to != null ? LocalDate.ofYearDay(to, 365) : null);
        return this.entryRepository.findBy(title, tagIds, fromDate, toDate);
    }

    public Entry findById(Long id) {
        return this.entryRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public Set<Entry> findByIds(Collection<Long> ids) {
        Set<Entry> result = this.entryRepository.findByIdIn(ids);
        if (ids.size() > result.size()) {
            this.logger.warn(
                    "Not able to find entries for all ids; searched for " + ids + ", but found only entries for IDs "
                            + result.stream().map(t -> t.getId()).collect(Collectors.toList())
            );
        }
        return result;
    }

    public void deleteById(Long id) {
        Entry entry = this.entryRepository.findById(id).orElseThrow(NotFoundException::new);
        this.entryRepository.delete(entry);
    }

    public WikipediaSummary findWikipediaSummaryForEntry(Long entryId) {
        Entry entry = this.findById(entryId);
        if (entry.getWikipediaPage() == null) {
            return this.wikipediaService.findWikipediaArticleSummary(entry.getTitle());
        } else {
            return this.wikipediaService.findWikipediaArticleSummary(entry.getWikipediaPage());
        }
    }

    public WikipediaSummary findWikipediaSummaryForTitle(String title) {
        if (title == null || title.length() < 3) {
            throw new InvalidParameterException();
        }
        return this.wikipediaService.findWikipediaArticleSummary(title);
    }

    public WikipediaSummary findRandom() {
        Long randomEntryId = this.entryRepository.findRandom().get(0);
        return this.findWikipediaSummaryForEntry(randomEntryId);
    }

    public List<Entry> search(String title, Long[] tagIds, Short from, Short to) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Entry> cr = cb.createQuery(Entry.class);
        Root<Entry> root = cr.from(Entry.class);
        cr = cr.select(root);
        List<Predicate> predicates = new ArrayList<>();

        if (title != null) {
            predicates.add(
                    cb.like(
                            cb.lower(root.get("title")),
                            cb.lower(cb.literal("%" + title + "%")))
            );
        }

        if (from != null) {
            root.fetch("dateRanges");
            LocalDate fromDate = LocalDate.ofYearDay(from, 1);
            predicates.add(
                    cb.greaterThanOrEqualTo(root.get("dateRanges").get("end"), fromDate)
            );
        }

        if (to != null) {
            root.fetch("dateRanges");
            LocalDate toDate = LocalDate.ofYearDay(to, 365);
            predicates.add(
                    cb.lessThanOrEqualTo(root.get("dateRanges").get("start"), toDate)
            );
        }

        if (tagIds != null && tagIds.length > 0) {
            // TODO: Fix
            // 1.: yields duplicates
            // 2.: evaluates to "ANY tag id IN" instead of "ALL tag ids IN"
            CriteriaBuilder.In<String> inClause = cb.in(root.get("tags").get("id"));
            for (Long tagId : tagIds) {
                inClause.value("" + tagId);
            }
            predicates.add(
                    inClause
            );
        }

        Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
        TypedQuery<Entry> q = em.createQuery(cr.where(cb.and(predicatesArray)));
        List<Entry> results = q.getResultList();
        return results;
    }

}
