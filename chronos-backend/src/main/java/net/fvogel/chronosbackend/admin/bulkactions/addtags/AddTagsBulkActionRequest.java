package net.fvogel.chronosbackend.admin.bulkactions.addtags;


import java.util.Set;

public record AddTagsBulkActionRequest(
        Set<Long> tagIds,
        Set<Long> entryIds
) {

}
