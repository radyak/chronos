package net.fvogel.chronos.data.domain.rest;

import jakarta.validation.Valid;
import net.fvogel.chronos.data.domain.model.CountResult;
import net.fvogel.chronos.data.domain.model.DataElement;
import net.fvogel.chronos.data.domain.model.Query;
import net.fvogel.chronos.data.domain.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/entries")
public class DataController {

    @Autowired
    private DataService dataService;

    @GetMapping()
    public List<DataElement> findAll(@ModelAttribute @Valid Query query) {
        return this.dataService.findAll(query);
    }

    @GetMapping("/statistics")
    public List<CountResult> statistics() {
        return this.dataService.statistics();
    }

    @GetMapping("/{id}")
    public DataElement findOne(
            @PathVariable("id") String id
    ) {
        return this.dataService.findById(id);
    }

}
