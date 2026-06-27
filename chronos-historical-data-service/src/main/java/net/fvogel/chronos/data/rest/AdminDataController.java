package net.fvogel.chronos.data.rest;

import jakarta.validation.Valid;
import net.fvogel.chronos.data.model.dto.UniqueCheckDTO;
import net.fvogel.chronos.data.model.internal.Entry;
import net.fvogel.chronos.data.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data/admin")
public class AdminDataController {

    private static final Logger logger = LoggerFactory.getLogger(AdminDataController.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private EntryFilterExtractor entryFilterExtractor;

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
    public boolean exists(@ModelAttribute @Valid UniqueCheckDTO uniqueCheckDto) {
        return dataService.isAttributeUnique(
                uniqueCheckDto.getKey(),
                uniqueCheckDto.getValue(),
                uniqueCheckDto.getElementId()
        );
    }

}
