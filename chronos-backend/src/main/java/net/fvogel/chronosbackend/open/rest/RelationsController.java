package net.fvogel.chronosbackend.open.rest;

import net.fvogel.chronosbackend.common.persistence.relations.model.Relation;
import net.fvogel.chronosbackend.common.service.RelationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/relations")
public class RelationsController {

    private RelationService relationService;

    public RelationsController(RelationService relationService) {
        this.relationService = relationService;
    }

    @GetMapping
    public Collection<Relation> all(
            @RequestParam(name = "ofEntry", required = false) Long entryId
    ) {
        if (entryId == null) {
            return this.relationService.findAll();
        } else {
            return this.relationService.findByEntryId(entryId);
        }
    }

}
