package net.fvogel.chronos.schema.shared.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(Set<String> deserializedStrings) {
        return deserializedStrings != null && !deserializedStrings.isEmpty() ? String.join(SPLIT_CHAR, deserializedStrings) : null;
    }

    @Override
    public Set<String> convertToEntityAttribute(String serializedString) {
        return serializedString != null ? new HashSet<>(Arrays.asList(serializedString.split(SPLIT_CHAR))) : null;
    }
}