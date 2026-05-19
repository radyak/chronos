package net.fvogel.chronos.data.it;

import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static net.fvogel.chronos.data.testutils.DataApiRequestMatcher.toExactlyMatchKeys;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DataApiIntegrationTest {

    @Nested
    class Paging extends BaseIntegrationTest {

        @Test
        void getDataWithoutParamsReturnsFirstPage() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/api/data"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(10))
                    .andExpect(toExactlyMatchKeys(
                                    "roman-empire",
                                    "otho",
                                    "vitellius",
                                    "vespasian",
                                    "titus",
                                    "domitian",
                                    "domitilla-the-elder",
                                    "caenis",
                                    "valerian-i",
                                    "gallienus"
                            )
                    );
        }

        @Test
        void getDataWithOnlyPageSizeParamReturnsFirstPage() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/api/data?pageSize=5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(5))
                    .andExpect(toExactlyMatchKeys(
                                    "roman-empire",
                                    "otho",
                                    "vitellius",
                                    "vespasian",
                                    "titus"
                            )
                    );
        }

        @Test
        void getDataWithPageSizeAndPageParamReturnsRespectivePage() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/api/data?pageSize=5&page=2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(5))
                    .andExpect(toExactlyMatchKeys(
                                    "domitian",
                                    "domitilla-the-elder",
                                    "caenis",
                                    "valerian-i",
                                    "gallienus"
                            )
                    );
        }

        @Test
        void getDataWithOutOfBoundPageParamReturnsEmptyPage() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/api/data?pageSize=5&page=12"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void getDataWithPageSizeOutOfBoundReturnsOnlyRelevantEntries() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/api/data?pageSize=100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(23));
        }

    }
}
