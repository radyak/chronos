package net.fvogel.chronosbackend.domain.schema.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.commons.model.schema.SchemaResponse;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronosbackend.domain.schema.rest.mappers.ModelMapper;
import net.fvogel.chronosbackend.domain.schema.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schema/admin")
public class AdminSchemaController {

    @Autowired
    SchemaService schemaService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/entities")
    public SchemaResponse createEntity(@Valid @RequestBody EntityPO entityPO) {
        schemaService.save(entityPO);
        entityPO = schemaService.getEntityByKey(entityPO.getKey());

        SchemaResponse response = new SchemaResponse();
        modelMapper.extractToResponseDto(entityPO, response);

        response.getMeta().setBase(entityPO.getKey());
        response.getMeta().setDepth(1);

        return response;
    }

    @PutMapping("/entities/{key}")
    public SchemaResponse updateEntity(@Valid @RequestBody EntityPO entityPO,
                                       @PathVariable String key) {
        // Ensure the entity already exists
        schemaService.assertEntityExistsByKey(key);

        schemaService.save(entityPO);

        SchemaResponse response = new SchemaResponse();
        modelMapper.extractToResponseDto(entityPO, response);

        response.getMeta().setBase(key);
        response.getMeta().setDepth(1);

        return response;
    }

    @DeleteMapping("/entities/{key}")
    public void deleteEntity(@PathVariable("key") String key) {
        schemaService.delete(key);
    }

}
