package net.fvogel.chronosbackend.config.web.converter;

import net.fvogel.chronosbackend.shared.exception.InvalidParameterException;
import net.fvogel.chronosbackend.shared.lang.SupportedLanguage;
import org.springframework.core.convert.converter.Converter;

public class StringToSupportedLanguagConverter implements Converter<String, SupportedLanguage> {
    @Override
    public SupportedLanguage convert(String source) {
        try {
            return SupportedLanguage.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException();
        }
    }
}
