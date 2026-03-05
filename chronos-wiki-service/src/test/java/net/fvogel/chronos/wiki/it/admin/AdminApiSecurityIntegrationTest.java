package net.fvogel.chronos.wiki.it.admin;

import net.fvogel.chronos.commons.lang.SupportedLanguage;
import net.fvogel.chronos.wiki.client.WikipediaApiClient;
import net.fvogel.chronos.wiki.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AdminApiSecurityIntegrationTest extends BaseIntegrationTest {

    @MockitoBean
    WikipediaApiClient wikipediaApiClient;

    @Test
    void unauthenticatedUserCannotSearchWikiArticle() throws Exception {
        mvc.perform(get("/api/admin/wiki/articles/search?q=otho"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(wikipediaApiClient);
    }

    @Test
    void unauthorizedUserCannotSearchWikiArticle() throws Exception {
        mvc.perform(get("/api/admin/wiki/articles/search?q=otho")
                .header("Authorization", authHeader("user"))
        ).andExpect(status().isForbidden());

        verifyNoInteractions(wikipediaApiClient);
    }

    @Test
    void adminRoleAuthorizedUserCanSearchWikiArticle() throws Exception {
        mvc.perform(get("/api/admin/wiki/articles/search?q=otho")
                .header("Authorization", adminAuthHeader())
        ).andExpect(status().isOk());

        verify(wikipediaApiClient, times(1)).searchWikipediaArticleInfos(eq("otho"), eq(SupportedLanguage.EN), eq(null));
    }

}
