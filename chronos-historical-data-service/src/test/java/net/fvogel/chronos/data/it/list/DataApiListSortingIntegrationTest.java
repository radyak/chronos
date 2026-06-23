package net.fvogel.chronos.data.it.list;

import com.jayway.jsonpath.JsonPath;
import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static net.fvogel.chronos.data.testutils.DataApiRequestMatcher.toExactlyMatchKeys;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiListSortingIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void getDataWithDateSortByParamReturnsSortedPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(10))
                .andExpect(toExactlyMatchKeys(
                        "vespasian",
                        "vitellius",
                        "otho",
                        "titus",
                        "domitian",
                        "valerian-i",
                        "victorinus",
                        "tacitus",
                        "aurelian",
                        "claudius-gothicus"
                ));
    }

    @Test
    void getDataWithAlphabeticalSortByParamAndPageSizeReturnsSortedPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=key&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(5))
                .andExpect(toExactlyMatchKeys(
                        "antiochus",
                        "aurelian",
                        "caenis",
                        "claudius-gothicus",
                        "domitian"
                ));
    }

    @Test
    void getDataWithInvertedSortOrderParamReturnsSortedPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=key&pageSize=5&sortOrder=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(5))
                .andExpect(toExactlyMatchKeys(
                        "zenobia",
                        "vitellius",
                        "victorinus",
                        "vespasian",
                        "valerian-i"
                ));
    }

    @Test
    void getDataWithSpecialRandomSortByParamReturnsSortedPage() throws Exception {
        String resultString1 = mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=random"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String resultString2 = mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=random"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> result1 = keyList(resultString1);
        List<String> result2 = keyList(resultString2);

        assertThat(result1, is(not(equalTo(result2))));
    }

    @Test
    void getDataThrowsBadRequestForInvalidSortOrderParam() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=key&sortOrder=invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDataIgnoresInvalidSortByParam() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=invalid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(10));
    }

    private List<String> keyList(String json) {
        return JsonPath.read(json, "$.entries[*].attributes.key");
    }

    @Test
    void getDataIncludesAppropriateMetadata() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?sortBy=key&sortOrder=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.query.sorting.length()").value(1))
                .andExpect(jsonPath("$.meta.query.sorting.[0].sortOrder").value("DESC"))
                .andExpect(jsonPath("$.meta.query.sorting.[0].sortBy").value("key"));

        mvc.perform(MockMvcRequestBuilders.get("/api/data/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.query.sorting.length()").value(1))
                .andExpect(jsonPath("$.meta.query.sorting.[0].sortOrder").value("ASC"))
                .andExpect(jsonPath("$.meta.query.sorting.[0].sortBy").value(IsNull.nullValue()));
    }

}
