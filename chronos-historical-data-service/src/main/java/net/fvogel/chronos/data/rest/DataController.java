package net.fvogel.chronos.data.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import net.fvogel.chronos.commons.exception.NotFoundException;
import net.fvogel.chronos.data.model.dto.CountResultDTO;
import net.fvogel.chronos.data.model.dto.DataResponseDTO;
import net.fvogel.chronos.data.model.internal.Entry;
import net.fvogel.chronos.data.model.internal.Relation;
import net.fvogel.chronos.data.model.internal.RelationRecord;
import net.fvogel.chronos.data.model.query.list.ListQuery;
import net.fvogel.chronos.data.model.query.list.Pagination;
import net.fvogel.chronos.data.model.query.list.Sorting;
import net.fvogel.chronos.data.model.query.mesh.MeshQuery;
import net.fvogel.chronos.data.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            @RequestParam(required = false) Set<String> relations,
            @RequestParam Map<String, String> queryParams) {
        MeshQuery query = new MeshQuery();
        query.setFilters(filterExtractor.extractFilterParams(queryParams));
        query.setRelations(relations);

        DataResponseDTO response = new DataResponseDTO();
        response.getMeta().setQuery(query);
        response.getMeta().setRequest(getFullURL(request));

        List<RelationRecord> records = this.dataService.mesh(query);

        // Deduping entries and relations
        Set<Entry> resultEntries = new HashSet<>();
        Set<Relation> resultRelations = new HashSet<>();
        records.forEach(record -> {
            resultEntries.addAll(record.getEntries());
            resultRelations.addAll(record.getRelations());
        });
        response.getEntries().addAll(resultEntries);
        response.getRelations().addAll(resultRelations);

        return response;
    }

    @GetMapping("/statistics")
    public List<CountResultDTO> statistics() {
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
