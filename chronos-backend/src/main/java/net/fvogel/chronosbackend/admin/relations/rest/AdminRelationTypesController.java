package net.fvogel.chronosbackend.admin.relations.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.common.persistence.relations.model.RelationType;
import net.fvogel.chronosbackend.common.service.RelationTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/relation-types")
public class AdminRelationTypesController {

    private RelationTypeService relationTypeService;

    public AdminRelationTypesController(RelationTypeService relationTypeService) {
        this.relationTypeService = relationTypeService;
    }

    @PostMapping
    public RelationType create(@Valid @RequestBody RelationType relationType) {
        return this.relationTypeService.save(relationType);
    }

    @GetMapping
    public List<RelationType> all() {
        return this.relationTypeService.findAll();
    }

    @GetMapping("/{id}")
    public RelationType getById(@PathVariable("id") Long id) {
        return this.relationTypeService.findById(id);
    }

    @PutMapping
    public RelationType update(@Valid @RequestBody RelationType relationType) {
        return this.relationTypeService.save(relationType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        this.relationTypeService.deleteById(id);
    }

}
