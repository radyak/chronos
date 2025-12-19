package net.fvogel.chronosbackend.domain.generic.rest;

import net.fvogel.chronosbackend.domain.generic.persistence.Entity;
import net.fvogel.chronosbackend.domain.generic.service.EntityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping()
    public List<Entity> findAll() {
        return this.entityService.findAll();
    }

//    @GetMapping("{id}")
//    public Map<String, String> findOne(
//            @PathVariable("id") String id
//    ) {
//        Entity entity = this.entityService.findById(id);
//
//        Map<String, String> map = new HashMap<>();
//        map.put("id", entity.id);
//        map.put("from", entity.from);
//        map.put("to", entity.to);
//        map.put("key", entity.key);
//        map.put("qid", entity.qid);
//
//        return map;
//    }

}
