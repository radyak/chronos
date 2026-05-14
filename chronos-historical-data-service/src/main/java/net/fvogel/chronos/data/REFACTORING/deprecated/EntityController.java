package net.fvogel.chronos.data.REFACTORING.deprecated;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/entities")
@Deprecated
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

    @GetMapping("/{id}")
    public Set<Map<String, Object>> findOne(
            @PathVariable("id") String id
    ) {
        return this.entityService.findWithRelationsById(id);
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
