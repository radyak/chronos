package net.fvogel.chronosbackend.admin.bulkactions;

import net.fvogel.chronosbackend.admin.bulkactions.addtags.AddTagsBulkActionService;
import net.fvogel.chronosbackend.admin.bulkactions.addtags.AddTagsBulkActionRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/bulk-actions")
public class BulkActionsController {

    private final AddTagsBulkActionService addTagsBulkActionService;

    public BulkActionsController(final AddTagsBulkActionService addTagsBulkActionService) {
        this.addTagsBulkActionService = addTagsBulkActionService;
    }

    @PutMapping("/entries/add-tags")
    public void addTagsToEntries(@RequestBody AddTagsBulkActionRequest request) {
        this.addTagsBulkActionService.addTagsToEntries(request.tagIds(), request.entryIds());
    }

}
