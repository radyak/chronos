package net.fvogel.chronosbackend.domain.schema.rest;

import net.fvogel.chronosbackend.commons.model.schema.SchemaResponse;
import net.fvogel.chronosbackend.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronosbackend.domain.schema.rest.mappers.ModelMapper;
import net.fvogel.chronosbackend.domain.schema.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/schema")
public class SchemaController {

    @Autowired
    SchemaService schemaService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping()
    public SchemaResponse getSchema() {
        Set<EntityPO> entities = schemaService.allEntities();

        SchemaResponse response = new SchemaResponse();
        entities.forEach(entity -> {
            modelMapper.extractToResponseDto(entity, response);
        });
        response.getMeta().setBase("*");
        response.getMeta().setDepth(1);

        return response;
    }

    @GetMapping("/{key}")
    public SchemaResponse getEntity(@PathVariable("key") String key) {
        EntityPO entityPO = schemaService.getEntityByKey(key);

        SchemaResponse response = new SchemaResponse();
        modelMapper.extractToResponseDto(entityPO, response);

        response.getMeta().setBase(key);
        response.getMeta().setDepth(1);

        return response;
    }

}
