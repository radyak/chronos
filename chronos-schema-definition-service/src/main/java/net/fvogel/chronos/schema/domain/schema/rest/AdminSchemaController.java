package net.fvogel.chronos.schema.domain.schema.rest;

import jakarta.validation.Valid;
import net.fvogel.chronos.commons.model.schema.SchemaResponse;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import net.fvogel.chronos.schema.domain.schema.rest.mappers.ModelMapper;
import net.fvogel.chronos.schema.domain.schema.service.SchemaService;
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
    public SchemaResponse createType(@Valid @RequestBody TypePO typePO) {
        schemaService.save(typePO);
        typePO = schemaService.getEntityByKey(typePO.getKey());

        SchemaResponse response = new SchemaResponse();
        modelMapper.extractToResponseDto(typePO, response);

        response.getMeta().setBase(typePO.getKey());
        response.getMeta().setDepth(1);

        return response;
    }

    @PutMapping("/entities/{key}")
    public SchemaResponse updateType(@Valid @RequestBody TypePO typePO,
                                     @PathVariable String key) {
        // Ensure the type already exists
        schemaService.assertTypeExistsByKey(key);

        schemaService.save(typePO);

        SchemaResponse response = new SchemaResponse();
        modelMapper.extractToResponseDto(typePO, response);

        response.getMeta().setBase(key);
        response.getMeta().setDepth(1);

        return response;
    }

    @DeleteMapping("/entities/{key}")
    public void deleteType(@PathVariable("key") String key) {
        schemaService.delete(key);
    }

}
