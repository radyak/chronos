package net.fvogel.chronosbackend.domain.generic.rest;

import net.fvogel.chronosbackend.domain.generic.persistence.Entity;
import net.fvogel.chronosbackend.domain.generic.persistence.LabelledEntity;
import net.fvogel.chronosbackend.domain.generic.service.EntityService;
import org.neo4j.driver.types.Node;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entities")
public class EntityController {

    private final EntityService entityService;

    public EntityController(EntityService entityService) {
        this.entityService = entityService;
    }

    @GetMapping
    public List<Entity> all() {
        return this.entityService.findAllNodes();
    }

    @GetMapping("/random")
    public Entity findRandom() {
        return this.entityService.findRandomEntity();
    }

}
