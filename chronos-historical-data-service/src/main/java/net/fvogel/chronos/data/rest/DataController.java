package net.fvogel.chronos.data.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import net.fvogel.chronos.commons.exception.InvalidParameterException;
import net.fvogel.chronos.data.REFACTORING.reuse.ReflectionUtils;
import net.fvogel.chronos.data.model.*;
import net.fvogel.chronos.data.model.dto.DataResponseDTO;
import net.fvogel.chronos.data.service.ConditionOperator;
import net.fvogel.chronos.data.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    private final Set<String> coveredQueryParams = new HashSet<>();
    @Autowired
    private DataService dataService;

    DataController() {
        this.coveredQueryParams.addAll(
                ReflectionUtils.getFieldNames(Pagination.class));
        this.coveredQueryParams.addAll(
                ReflectionUtils.getFieldNames(Sorting.class));
    }

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
        query.setFilters(getFilterParams(queryParams));

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

    private List<Filter> getFilterParams(Map<String, String> queryParams) {
        Map<String, String> filteredQueryParams = queryParams.entrySet().stream()
                .filter(e -> !this.coveredQueryParams.contains(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        return filteredQueryParams.entrySet().stream()
                .map((Map.Entry<String, String> filterParam) -> {
                    // Asserts
                    if (filterParam.getKey() == null || filterParam.getKey().trim().isEmpty() || filterParam.getValue() == null || filterParam.getValue().trim().isEmpty()) {
                        logger.warn("Invalid filter: {} = {}", filterParam.getKey(), filterParam.getValue());
                        throw new InvalidParameterException();
                    }
                    String[] filterComponents = filterParam.getKey().split(":");


                    // Mapping
                    Filter filter = new Filter();

                    // Mapping: Field
                    filter.setAttribute(filterComponents[0]);

                    // Mapping: Operator
                    ConditionOperator operator = ConditionOperator.EQUAL;
                    if (filterComponents.length == 2) {
                        operator = ConditionOperator.fromValue(filterComponents[1]);
                        if (operator == null) {
                            logger.warn("Invalid filter operator: {} = {}", filterParam.getKey(), filterParam.getValue());
                            throw new InvalidParameterException();
                        }
                    }
                    filter.setOperator(operator);

                    // Mapping: Value
                    if ("null".equalsIgnoreCase(filterParam.getValue())) {
                        filter.setValue(null);
                    } else {
                        filter.setValue(filterParam.getValue());
                    }

                    return filter;
                })
                .toList();
    }


}
