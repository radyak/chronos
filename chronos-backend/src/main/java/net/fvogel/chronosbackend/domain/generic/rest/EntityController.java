package net.fvogel.chronosbackend.domain.generic.rest;

import net.fvogel.chronosbackend.domain.generic.persistence.Entity;
import net.fvogel.chronosbackend.domain.generic.service.EntityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entities")
public class EntityController {

    private final EntityService entityService;

    public EntityController(EntityService entityService) {
        this.entityService = entityService;
    }

    @GetMapping("/random")
    public Entity findRandom() {
        return this.entityService.findRandomEntityWithQid();
    }

}
