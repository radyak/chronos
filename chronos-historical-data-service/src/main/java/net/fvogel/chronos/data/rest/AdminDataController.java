package net.fvogel.chronos.data.rest;

import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
    private FilterExtractor filterExtractor;

    @PostMapping()
    public Entry create(@RequestBody Entry entry) {
        dataService.create(entry);
        return entry;
    }

}
