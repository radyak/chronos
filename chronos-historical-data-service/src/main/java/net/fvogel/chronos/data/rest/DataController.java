package net.fvogel.chronos.data.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import net.fvogel.chronos.data.model.*;
import net.fvogel.chronos.data.model.dto.DataResponseDTO;
import net.fvogel.chronos.data.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DataService dataService;
    @Autowired
    private FilterExtractor filterExtractor;

    public static String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    @GetMapping()
    public DataResponseDTO findAll(
            HttpServletRequest request,
            @ModelAttribute @Valid Pagination pagination,
            @ModelAttribute @Valid Sorting sorting,
            @RequestParam Map<String, String> queryParams) {
        DataQuery query = new DataQuery();
        query.setPagination(pagination);
        query.setSorting(Collections.singletonList(sorting));
        query.setFilters(filterExtractor.extractFilterParams(queryParams));

        List<Entry> entries = this.dataService.findAll(query);

        DataResponseDTO response = new DataResponseDTO();
        response.getMeta().setQuery(query);
        response.getMeta().setRequest(getFullURL(request));
        response.setEntries(entries);

        return response;
    }

    @GetMapping("/statistics")
    public List<CountResult> statistics() {
        return this.dataService.statistics();
    }

    @GetMapping("/{id}")
    public Entry findOne(
            @PathVariable("id") String id
    ) {
        return this.dataService.findById(id);
    }


}
