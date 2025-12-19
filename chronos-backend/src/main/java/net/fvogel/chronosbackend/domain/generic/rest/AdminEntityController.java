package net.fvogel.chronosbackend.domain.generic.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.domain.generic.persistence.Entity;
import net.fvogel.chronosbackend.domain.generic.service.EntityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/entities")
public class AdminEntityController {

    private final EntityService entityService;

    public AdminEntityController(EntityService entityService) {
        this.entityService = entityService;
    }

    @PostMapping("/{type}")
    public Entity createEntity(@PathVariable("type") String type,
                               @RequestBody @Valid Entity entity) {
        return this.entityService.createEntity(type, entity);
    }

    @PutMapping("/{type}/{id}")
    public Entity updateEntity(@PathVariable("type") String type,
                               @PathVariable("id") String id,
                               @RequestBody @Valid Entity entity) {
        return this.entityService.updateEntity(type, id, entity);
    }

    @DeleteMapping("/{type}/{id}")
    public void deleteEntity(@PathVariable("type") String type,
                             @PathVariable("id") String id) {
        this.entityService.deleteEntity(type, id);
    }

}
