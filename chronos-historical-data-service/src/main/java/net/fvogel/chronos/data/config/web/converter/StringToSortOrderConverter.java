package net.fvogel.chronos.data.config.web.converter;

import net.fvogel.chronos.commons.exception.InvalidParameterException;
import net.fvogel.chronos.data.model.query.list.SortOrder;
import org.springframework.core.convert.converter.Converter;

public class StringToSortOrderConverter implements Converter<String, SortOrder> {
    @Override
    public SortOrder convert(String source) {
        try {
            return SortOrder.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException();
        }
    }
}
