package net.fvogel.chronosbackend.admin.relations.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.common.persistence.relations.model.Relation;
import net.fvogel.chronosbackend.common.service.RelationService;
import net.fvogel.chronosbackend.open.rest.RelationsController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/relations")
public class AdminRelationsController extends RelationsController {

    public AdminRelationsController(RelationService relationService) {
        super(relationService);
    }

    @PostMapping
    public Relation create(@Valid @RequestBody Relation relation) {
        return this.relationService.save(relation);
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
