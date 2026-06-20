package net.fvogel.chronos.data.it.paging;

import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiPagingIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void getDataWithoutParamsReturnsFirstPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(10));
    }

    @Test
    void getDataWithOnlyPageSizeParamReturnsFirstPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(5));
    }

    @Test
    void getDataWithPageSizeAndPageParamReturnsRespectivePage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?pageSize=5&page=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(5));
    }

    @Test
    void getDataWithOutOfBoundPageParamReturnsEmptyPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?pageSize=5&page=12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(0));
    }

    @Test
    void getDataWithPageSizeOutOfBoundReturnsOnlyRelevantEntries() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?pageSize=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(23));
    }

    @Test
    void getDataThrowsBadRequestWithInvalidPageSizeParam() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?pageSize=-2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDataThrowsBadRequestWithInvalidPageParam() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?page=0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDataIncludesAppropriateMetadata() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/data/list?pageSize=5&page=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.query.pagination.pageSize").value(5))
                .andExpect(jsonPath("$.meta.query.pagination.page").value(2));

        mvc.perform(MockMvcRequestBuilders.get("/api/data/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.query.pagination.pageSize").value(10))
                .andExpect(jsonPath("$.meta.query.pagination.page").value(1));
    }

}
