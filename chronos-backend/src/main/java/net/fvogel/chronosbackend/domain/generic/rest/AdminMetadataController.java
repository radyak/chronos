package net.fvogel.chronosbackend.domain.generic.rest;

import net.fvogel.chronosbackend.domain.generic.model.EntityMetadata;
import net.fvogel.chronosbackend.domain.generic.service.MetadataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/metadata")
public class AdminMetadataController {

    private final MetadataService metadataService;

    public AdminMetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @GetMapping("")
    public List<EntityMetadata> listNodes() {
        return this.metadataService.getAllMetaData();
    }

}
