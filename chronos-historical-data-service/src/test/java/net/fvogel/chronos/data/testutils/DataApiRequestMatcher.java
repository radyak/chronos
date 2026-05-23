package net.fvogel.chronos.data.testutils;

import com.jayway.jsonpath.JsonPath;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class DataApiRequestMatcher {

    public static ResultMatcher toContainKeys(String... expectedKeys) {
        return mvcResult -> {
            Collection<String> presentKeys = extractKeys(mvcResult);

            List<String> missingKeys = Arrays.stream(expectedKeys)
                    .filter(expectedKey -> !presentKeys.contains(expectedKey))
                    .toList();

            if (!missingKeys.isEmpty()) {
                throw new AssertionError(
                        """
                                Not all keys found.
                                From %s keys, %s was/were missing: %s
                                Expected: %s
                                Actual content: %s
                                """.formatted(
                                presentKeys.size(),
                                missingKeys.size(),
                                String.join(",", missingKeys),
                                String.join(",", expectedKeys),
                                String.join(",", presentKeys))
                );
            }
        };
    }

    public static ResultMatcher toExactlyMatchKeys(String... expectedKeys) {
        return mvcResult -> {
            Collection<String> presentKeys = extractKeys(mvcResult);

            if (!Arrays.asList(expectedKeys).equals(presentKeys)) {
                throw new AssertionError(
                        """
                                Other keys present than expected.
                                Expected: %s
                                Actual: %s
                                """.formatted(
                                String.join(",", expectedKeys),
                                String.join(",", presentKeys))
                );
            }
        };
    }

    private static List<String> extractKeys(MvcResult mvcResult) throws UnsupportedEncodingException {
        String jsonPath = "$.entries.[*].properties.key";
        String responseJson = mvcResult.getResponse().getContentAsString();
        Collection<Object> values = JsonPath.read(responseJson, jsonPath);

        if (values == null) {
            throw new AssertionError(
                    "No value found at jsonPath: " + jsonPath
            );
        }

        return values.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toList();
    }

}
