package net.fvogel.chronos.schema.it;

import net.fvogel.chronos.schema.domain.schema.persistence.model.type.TypePO;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.TypePORepository;
import net.fvogel.chronos.schema.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(properties = {"spring.cache.type=simple"})
public class CachingIntegrationTest extends BaseIntegrationTest {

    @MockitoSpyBean
    TypePORepository typePORepository;

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
        Mockito.reset(typePORepository);
    }

    @Test
    void singleTypeIsCached() throws Exception {
        getType("Territory")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("Territory"))
                .andExpect(jsonPath("$.types.elements.length()").value(2));

        verify(typePORepository, times(1)).findByKey(eq("Territory"));

        getType("Territory")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("Territory"))
                .andExpect(jsonPath("$.types.elements.length()").value(2));

        verifyNoMoreInteractions(typePORepository);
    }

    @Test
    void singleTypeCachesDoNotCollide() throws Exception {
        getType("Territory").andExpect(status().isOk());
        getType("Person").andExpect(status().isOk());
        verify(typePORepository, times(1)).findByKey(eq("Territory"));
        verify(typePORepository, times(1)).findByKey(eq("Person"));

        getType("Territory").andExpect(status().isOk());
        getType("Person").andExpect(status().isOk());
        verifyNoMoreInteractions(typePORepository);
    }

    @Test
    void completeSchemaIsCached() throws Exception {
        mvc.perform(get("/api/schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("*"))
                .andExpect(jsonPath("$.types.elements.length()").value(2));

        verify(typePORepository, times(1)).findAll();

        mvc.perform(get("/api/schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("*"))
                .andExpect(jsonPath("$.types.elements.length()").value(2));

        verifyNoMoreInteractions(typePORepository);
    }

    @Test
    void updatingSingleTypeUpdatesCaches() throws Exception {
        getType("Person").andExpect(status().isOk());
        getType("Person").andExpect(status().isOk())
                .andExpect(jsonPath("$.types.elements[?(@.key == 'Person')].explanation").isEmpty());
        verify(typePORepository, times(1)).findByKey("Person");

        TypePO type = loadType("Person");
        type.setExplanation("An individual human");

        mvc.perform(put("/api/schema/admin/types/Person")
                .content(objectMapper.writeValueAsString(type))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Mockito.reset(typePORepository);

        // Retrieve type again - Cache PUT updated the TYPE cache already
        getType("Person").andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("Person"))
                .andExpect(jsonPath("$.types.elements.length()").value(2))
                .andExpect(jsonPath("$.types.elements[?(@.key == 'Person')].explanation").value("An individual human"));

        verify(typePORepository, times(1)).findByKey("Person");


        // Retrieve complete schema again - Cache EVICT had to empty SCHEMA cache
        mvc.perform(get("/api/schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("*"))
                .andExpect(jsonPath("$.types.elements.length()").value(2));

        verify(typePORepository, times(1)).findAll();
    }

    @Test
    void deletingSingleTypeUpdatesCaches() throws Exception {
        getType("Person").andExpect(status().isOk());
        getType("Person").andExpect(status().isOk());
        verify(typePORepository, times(1)).findByKey("Person");

        mvc.perform(delete("/api/schema/admin/types/Person")
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Mockito.reset(typePORepository);

        getType("Person").andExpect(status().isNotFound());
        verify(typePORepository, times(1)).findByKey("Person");

        mvc.perform(get("/api/schema")).andExpect(status().isOk())
                .andExpect(jsonPath("$.types.elements.length()").value(1));
        verify(typePORepository, times(1)).findAll();
    }

}
