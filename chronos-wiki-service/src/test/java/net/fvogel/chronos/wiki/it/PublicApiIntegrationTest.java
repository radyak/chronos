package net.fvogel.chronos.wiki.it;

import net.fvogel.chronos.commons.lang.SupportedLanguage;
import net.fvogel.chronos.wiki.client.WikipediaApiClient;
import net.fvogel.chronos.wiki.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;

import static net.fvogel.chronos.wiki.testutils.WikiArticleTestData.defaultWikipediaEntityResultDto;
import static net.fvogel.chronos.wiki.testutils.WikiArticleTestData.defaultWikipediaQueryResultDto;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class PublicApiIntegrationTest extends BaseIntegrationTest {

    @MockitoBean
    WikipediaApiClient wikipediaApiClient;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
        Mockito.reset(wikipediaApiClient);
    }

    @Test
    void canGetWikipediaSummaryById() throws Exception {
        // GIVEN
        when(wikipediaApiClient.getEntityByQid(eq("Q1234"), eq(SupportedLanguage.EN))).thenReturn(defaultWikipediaEntityResultDto());
        when(wikipediaApiClient.queryWikipediaArticleByTitle(eq("Otho"), eq(SupportedLanguage.EN))).thenReturn(defaultWikipediaQueryResultDto());

        // WHEN
        mvc.perform(get("/api/wiki/articles/{id}", "Q1234")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Otho"))
                .andExpect(jsonPath("$.pageUrl").value("https://wiki/otho"))
                .andExpect(jsonPath("$.image.url").value("https://wiki/otho/otho.jpg"));

        // THEN
        verify(wikipediaApiClient, times(1)).getEntityByQid(eq("Q1234"), eq(SupportedLanguage.EN));
        verify(wikipediaApiClient, times(1)).queryWikipediaArticleByTitle(eq("Otho"), eq(SupportedLanguage.EN));
    }

    @Test
    void cannotGetWikipediaSummaryWithInvalidId() throws Exception {
        // WHEN
        mvc.perform(get("/api/wiki/articles/{id}", "1234")).andExpect(status().isBadRequest());

        // THEN
        verifyNoInteractions(wikipediaApiClient);
    }

    @Test
    void canGetWikipediaSummaryByIdWithLanguage() throws Exception {
        // GIVEN
        when(wikipediaApiClient.getEntityByQid(eq("Q1234"), eq(SupportedLanguage.IT))).thenReturn(defaultWikipediaEntityResultDto());
        when(wikipediaApiClient.queryWikipediaArticleByTitle(eq("Othone"), eq(SupportedLanguage.IT))).thenReturn(defaultWikipediaQueryResultDto());

        // WHEN
        mvc.perform(get("/api/wiki/articles/{id}?lang={lang}", "Q1234", "it")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Otho"));

        // THEN
        verify(wikipediaApiClient, times(1)).getEntityByQid(eq("Q1234"), eq(SupportedLanguage.IT));
        verify(wikipediaApiClient, times(1)).queryWikipediaArticleByTitle(eq("Othone"), eq(SupportedLanguage.IT));
    }

    @Test
    void cannotGetWikipediaSummaryForUnknownId() throws Exception {
        // WHEN
        mvc.perform(get("/api/wiki/articles/{id}", "Q2345")).andExpect(status().isNotFound());

        // THEN
        verify(wikipediaApiClient, times(1)).getEntityByQid(eq("Q2345"), eq(SupportedLanguage.EN));
        verifyNoMoreInteractions(wikipediaApiClient);
    }

    @Test
    void cannotGetWikipediaSummaryIfQueryWikipediaArticleByTitleFails() throws Exception {
        // GIVEN
        when(wikipediaApiClient.getEntityByQid(eq("Q1234"), eq(SupportedLanguage.EN))).thenReturn(defaultWikipediaEntityResultDto());
        when(wikipediaApiClient.queryWikipediaArticleByTitle(eq("Otho"), eq(SupportedLanguage.EN))).thenReturn(null);

        // WHEN
        mvc.perform(get("/api/wiki/articles/{id}", "Q1234")).andExpect(status().isNotFound());

        // THEN
        verify(wikipediaApiClient, times(1)).getEntityByQid(eq("Q1234"), eq(SupportedLanguage.EN));
        verify(wikipediaApiClient, times(1)).queryWikipediaArticleByTitle(eq("Otho"), eq(SupportedLanguage.EN));
    }

}
