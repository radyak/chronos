package net.fvogel.chronos.data.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.data.model.*;
import net.fvogel.chronos.data.model.dto.DataResponseDTO;
import net.fvogel.chronos.data.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private FilterExtractor filterExtractor;

    private static String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    @GetMapping("/list")
    public DataResponseDTO list(
            HttpServletRequest request,
            @ModelAttribute @Valid Pagination pagination,
            @ModelAttribute @Valid Sorting sorting,
            @RequestParam Map<String, String> queryParams) {
        ListQuery query = new ListQuery();
        query.setPagination(pagination);
        query.setSorting(Collections.singletonList(sorting));
        query.setFilters(filterExtractor.extractFilterParams(queryParams));

        List<Entry> entries = this.dataService.list(query);

        DataResponseDTO response = new DataResponseDTO();
        response.getMeta().setQuery(query);
        response.getMeta().setRequest(getFullURL(request));
        response.getEntries().addAll(entries);

        return response;
    }

    @GetMapping("/mesh")
    public DataResponseDTO mesh(
            HttpServletRequest request,
            @RequestParam Map<String, String> queryParams) {
        ListQuery query = new ListQuery();
        query.setFilters(filterExtractor.extractFilterParams(queryParams));

        DataResponseDTO response = new DataResponseDTO();
        response.getMeta().setQuery(query);
        response.getMeta().setRequest(getFullURL(request));

        List<RelationRecord> records = this.dataService.mesh(query);

        // Deduping entries and relations
        Set<Entry> entries = new HashSet<>();
        Set<Relation> relations = new HashSet<>();
        records.forEach(record -> {
            entries.addAll(record.getEntries());
            relations.addAll(record.getRelations());
        });
        response.getEntries().addAll(entries);
        response.getRelations().addAll(relations);

        return response;
    }

    @GetMapping("/statistics")
    public List<CountResult> statistics() {
        return this.dataService.statistics();
    }

    @GetMapping("/{key}")
    public Entry findOne(
            @PathVariable("key") String key
    ) {
        return this.dataService.findByKey(key)
                .orElseThrow(NotFoundException::new);
    }

}
