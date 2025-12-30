package net.fvogel.chronosbackend.domain.schema.rest;

import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronosbackend.domain.schema.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schema/admin")
public class AdminSchemaController {

    @Autowired
    SchemaService schemaService;

    @PostMapping("/entities")
    public EntityPO createEntity(@RequestBody EntityPO entityPO) {
        return schemaService.save(entityPO);
    }

    @PutMapping("/entities/{key}")
    public EntityPO updateEntity(@RequestBody EntityPO entityPO) {
        return schemaService.save(entityPO);
    }

    @DeleteMapping("/entities/{key}")
    public void deleteEntity(@PathVariable("key") String key) {
        schemaService.delete(key);
    }

}
