package net.fvogel.chronosbackend.common.service;

import net.fvogel.chronosbackend.common.exception.InvalidParameterException;
import net.fvogel.chronosbackend.common.exception.NotFoundException;
import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;
import net.fvogel.chronosbackend.common.persistence.entries.repo.EntryRepository;
import net.fvogel.chronosbackend.wikipedia.model.WikipediaSummary;
import net.fvogel.chronosbackend.wikipedia.service.WikipediaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class EntriesService {

    private EntryRepository entryRepository;
    private WikipediaService wikipediaService;

    public EntriesService(EntryRepository entryRepository,
                          WikipediaService wikipediaService) {
        this.entryRepository = entryRepository;
        this.wikipediaService = wikipediaService;
    }

    public Entry save(Entry entry) {
        return this.entryRepository.save(entry);
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

}
