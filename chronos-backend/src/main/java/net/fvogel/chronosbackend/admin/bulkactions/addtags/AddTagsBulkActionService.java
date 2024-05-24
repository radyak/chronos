package net.fvogel.chronosbackend.admin.bulkactions.addtags;

import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;
import net.fvogel.chronosbackend.common.persistence.tags.model.Tag;
import net.fvogel.chronosbackend.common.service.EntriesService;
import net.fvogel.chronosbackend.common.service.TagsService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AddTagsBulkActionService {

    private TagsService tagsService;
    private EntriesService entriesService;

    AddTagsBulkActionService(
            TagsService tagsService,
            EntriesService entriesService) {
        this.tagsService = tagsService;
        this.entriesService = entriesService;
    }

    public void addTagsToEntries(Set<Long> tagIds, Set<Long> entryIds) {
        Set<Tag> tags = this.tagsService.findByIds(tagIds);
        Set<Entry> entries = this.entriesService.findByIds(entryIds);
        for (Entry entry : entries) {
            entry.getTags().addAll(tags);
        }
        this.entriesService.saveAll(entries);
    }
}
