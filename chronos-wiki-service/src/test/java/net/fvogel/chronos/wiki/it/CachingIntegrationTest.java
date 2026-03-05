package net.fvogel.chronos.wiki.it;

import net.fvogel.chronos.commons.lang.SupportedLanguage;
import net.fvogel.chronos.wiki.client.WikipediaApiClient;
import net.fvogel.chronos.wiki.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;

import static net.fvogel.chronos.wiki.testutils.WikiArticleTestData.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(properties = {"spring.cache.type=simple"})
public class CachingIntegrationTest extends BaseIntegrationTest {

    @MockitoBean
    WikipediaApiClient wikipediaApiClient;

    @Autowired
    private CacheManager cacheManager;

    private void clearCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @BeforeEach
    public void setup() throws IOException {
        // Caches need to be cleared to properly clean and set up test data
        this.clearCaches();
        super.setup();

        this.clearCaches();
        Mockito.reset(wikipediaApiClient);
    }

    @Test
    void wikipediaSummaryByIdIsCached() throws Exception {
        // GIVEN
        when(wikipediaApiClient.getEntityByQid(eq("Q1234"), eq(SupportedLanguage.EN))).thenReturn(defaultWikipediaEntityResultDto());
        when(wikipediaApiClient.queryWikipediaArticleByTitle(eq("Otho"), eq(SupportedLanguage.EN))).thenReturn(defaultWikipediaQueryResultDto());

        // WHEN
        mvc.perform(get("/api/wiki/articles/{id}", "Q1234")).andExpect(status().isOk());

        // THEN
        verify(wikipediaApiClient, times(1)).getEntityByQid(eq("Q1234"), eq(SupportedLanguage.EN));
        verify(wikipediaApiClient, times(1)).queryWikipediaArticleByTitle(eq("Otho"), eq(SupportedLanguage.EN));


        // WHEN (requesting again)
        mvc.perform(get("/api/wiki/articles/{id}", "Q1234")).andExpect(status().isOk());

        // THEN (no interactions, cached)
        verifyNoMoreInteractions(wikipediaApiClient);
    }

    @Test
    void articleSearchIsCached() throws Exception {
        // GIVEN
        when(wikipediaApiClient.searchWikipediaArticleInfos(eq("otho"), eq(SupportedLanguage.EN), eq(null))).thenReturn(defaultWikipediaSearchResultDto());

        // WHEN
        mvc.perform(get("/api/admin/wiki/articles/search?q=otho")
                .header("Authorization", adminAuthHeader())
        ).andExpect(status().isOk());

        // THEN
        verify(wikipediaApiClient, times(1)).searchWikipediaArticleInfos(eq("otho"), eq(SupportedLanguage.EN), eq(null));


        // WHEN (requesting again)
        mvc.perform(get("/api/admin/wiki/articles/search?q=otho")
                .header("Authorization", adminAuthHeader())
        ).andExpect(status().isOk());

        // THEN (no interactions, cached)
        verifyNoMoreInteractions(wikipediaApiClient);
    }


}
