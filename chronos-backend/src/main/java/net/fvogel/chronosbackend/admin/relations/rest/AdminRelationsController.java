package net.fvogel.chronosbackend.admin.relations.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.common.persistence.relations.model.Relation;
import net.fvogel.chronosbackend.common.service.RelationService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/admin/relations")
public class AdminRelationsController {

    private RelationService relationService;

    public AdminRelationsController(RelationService relationService) {
        this.relationService = relationService;
    }

    @PostMapping
    public Relation create(@Valid @RequestBody Relation relation) {
        return this.relationService.save(relation);
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

    @GetMapping("/{id}")
    public Relation getById(@PathVariable("id") Long id) {
        return this.relationService.findById(id);
    }

    @PutMapping
    public Relation update(@Valid @RequestBody Relation relation) {
        return this.relationService.save(relation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        this.relationService.deleteById(id);
    }

}
