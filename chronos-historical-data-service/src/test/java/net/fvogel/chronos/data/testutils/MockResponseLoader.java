package net.fvogel.chronos.data.testutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronos.commons.model.schema.SchemaResponse;

import java.io.IOException;
import java.io.InputStream;

public class MockResponseLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static SchemaResponse loadMockSchemaResponse(String file) {
        String filePath = "mock-responses/" + file;

        InputStream is = MockResponseLoader.class
                .getClassLoader()
                .getResourceAsStream(filePath);

        if (is == null) {
            throw new IllegalStateException("Resource not found");
        }

        try {
            return mapper.readValue(is, SchemaResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file " + filePath, e);
        }
    }

}
