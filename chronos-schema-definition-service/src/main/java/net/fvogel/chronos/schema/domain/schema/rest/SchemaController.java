package net.fvogel.chronos.schema.domain.schema.rest;

import net.fvogel.chronos.commons.model.schema.SchemaResponse;
import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import net.fvogel.chronos.schema.domain.schema.rest.mappers.ModelMapper;
import net.fvogel.chronos.schema.domain.schema.service.SchemaService;
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
        Set<TypePO> types = schemaService.allTypes();

        SchemaResponse response = new SchemaResponse();
        types.forEach(type -> {
            modelMapper.extractToResponseDto(type, response);
        });
        response.getMeta().setBase("*");
        response.getMeta().setDepth(1);

        return response;
    }

    @GetMapping("/{key}")
    public SchemaResponse getEntity(@PathVariable("key") String key) {
        TypePO typePO = schemaService.getEntityByKey(key);

        SchemaResponse response = new SchemaResponse();
        modelMapper.extractToResponseDto(typePO, response);

        response.getMeta().setBase(key);
        response.getMeta().setDepth(1);

        return response;
    }

}
