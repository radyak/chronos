package net.fvogel.chronos.data.rest;

import net.fvogel.chronos.commons.exception.InvalidParameterException;
import net.fvogel.chronos.data.model.query.ConditionOperator;
import net.fvogel.chronos.data.model.query.EntryFilter;
import net.fvogel.chronos.data.model.query.list.Pagination;
import net.fvogel.chronos.data.model.query.list.Sorting;
import net.fvogel.chronos.data.model.query.mesh.MeshQuery;
import net.fvogel.chronos.data.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EntryFilterExtractor {

    private static final Logger logger = LoggerFactory.getLogger(EntryFilterExtractor.class);

    private final Set<String> coveredQueryParams = new HashSet<>();

    EntryFilterExtractor() {
        this.coveredQueryParams.addAll(
                ReflectionUtils.getFieldNames(Pagination.class));
        this.coveredQueryParams.addAll(
                ReflectionUtils.getFieldNames(Sorting.class));
        this.coveredQueryParams.addAll(
                ReflectionUtils.getFieldNames(MeshQuery.class));
    }

    public List<EntryFilter> extractFilterParams(Map<String, String> queryParams) {
        return queryParams.entrySet().stream()
                .filter(e -> !this.coveredQueryParams.contains(e.getKey()))
                .map((Map.Entry<String, String> filterParam) -> {
                    // Asserts
                    if (filterParam.getKey() == null || filterParam.getKey().trim().isEmpty() || filterParam.getValue() == null || filterParam.getValue().trim().isEmpty()) {
                        logger.warn("Invalid filter: {} = {}", filterParam.getKey(), filterParam.getValue());
                        throw new InvalidParameterException();
                    }
                    String[] filterComponents = filterParam.getKey().split(":");


                    // Mapping
                    EntryFilter entryFilter = new EntryFilter();


                    // Mapping: Labels
                    if ("labels".equalsIgnoreCase(filterParam.getKey())) {
                        entryFilter.setLabels(List.of(filterParam.getValue().split(",")));
                        return entryFilter;
                    }

                    // Mapping: Field
                    entryFilter.setAttribute(filterComponents[0]);

                    // Mapping: Operator
                    ConditionOperator operator = ConditionOperator.EQUAL;
                    if (filterComponents.length == 2) {
                        operator = ConditionOperator.fromValue(filterComponents[1]);
                        if (operator == null) {
                            logger.warn("Invalid filter operator: {} = {}", filterParam.getKey(), filterParam.getValue());
                            throw new InvalidParameterException();
                        }
                    }
                    entryFilter.setOperator(operator);

                    // Mapping: Value
                    if ("null".equalsIgnoreCase(filterParam.getValue())) {
                        entryFilter.setValue(null);
                    } else {
                        entryFilter.setValue(filterParam.getValue());
                    }

                    return entryFilter;
                })
                .toList();
    }
}
