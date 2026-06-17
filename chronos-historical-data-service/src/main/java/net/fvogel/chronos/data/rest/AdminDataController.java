package net.fvogel.chronos.data.rest;

import jakarta.validation.Valid;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.dto.UniqueCheckDto;
import net.fvogel.chronos.data.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data/admin")
public class AdminDataController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDataController.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private FilterExtractor filterExtractor;

    @PostMapping()
    public Entry createEntry(@RequestBody Entry entry) {
        return dataService.create(entry);
    }

    @PutMapping("/{key}")
    public Entry updateEntry(@RequestBody Entry entry,
                             @PathVariable String key) {
        return dataService.update(key, entry);
    }

    @DeleteMapping("/{key}")
    public void delete(@PathVariable String key) {
        dataService.deleteByKey(key);
    }

    /**
     * Checks if a given attribute value already exists.
     * For checking updates to a node, the own attribute value of the respective node - specified by elementId -
     * must be ignored. For new nodes, the elementId is null, i.e. the database is checked without any ignores.
     *
     * @param uniqueCheckDto
     * @return true, if the attribute only exists on the node with specified elementId, otherwise false
     */
    @GetMapping("/unique")
    public boolean exists(@ModelAttribute @Valid UniqueCheckDto uniqueCheckDto) {
        return dataService.isAttributeUnique(
                uniqueCheckDto.getKey(),
                uniqueCheckDto.getValue(),
                uniqueCheckDto.getElementId()
        );
    }

}
