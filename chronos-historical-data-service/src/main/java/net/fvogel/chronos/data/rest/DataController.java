package net.fvogel.chronos.data.rest;

import jakarta.validation.Valid;
import net.fvogel.chronos.data.REFACTORING.reuse.ReflectionUtils;
import net.fvogel.chronos.data.model.*;
import net.fvogel.chronos.data.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final Set<String> coveredQueryParams = new HashSet<>();
    @Autowired
    private DataService dataService;

    DataController() {
        this.coveredQueryParams.addAll(
                ReflectionUtils.getFieldNames(Pagination.class));
        this.coveredQueryParams.addAll(
                ReflectionUtils.getFieldNames(Sorting.class));
    }

    @GetMapping()
    public List<Entry> findAll(
            @ModelAttribute @Valid Pagination pagination,
            @ModelAttribute @Valid Sorting sorting,
            @RequestParam Map<String, String> queryParams) {
        DataQuery query = new DataQuery();
        query.setPagination(pagination);
        query.setSorting(sorting);
        query.setFilters(getFilterParams(queryParams));

        return this.dataService.findAll(query);
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

    private Map<String, String> getFilterParams(Map<String, String> queryParams) {
        return queryParams.entrySet().stream()
                .filter(e -> !this.coveredQueryParams.contains(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

}
