package net.fvogel.chronos.schema.it;

import net.fvogel.chronos.schema.domain.schema.persistence.model.entity.EntityPO;
import net.fvogel.chronos.schema.domain.schema.persistence.repository.EntityPORepository;
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
    EntityPORepository entityPORepository;

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
        Mockito.reset(entityPORepository);
    }

    @Test
    void singleEntityIsCached() throws Exception {
        getEntity("Territory")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("Territory"))
                .andExpect(jsonPath("$.entities.elements.length()").value(2));

        verify(entityPORepository, times(1)).findByKey(eq("Territory"));

        getEntity("Territory")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("Territory"))
                .andExpect(jsonPath("$.entities.elements.length()").value(2));

        verifyNoMoreInteractions(entityPORepository);
    }

    @Test
    void singleEntityCachesDoNotCollide() throws Exception {
        getEntity("Territory").andExpect(status().isOk());
        getEntity("Person").andExpect(status().isOk());
        verify(entityPORepository, times(1)).findByKey(eq("Territory"));
        verify(entityPORepository, times(1)).findByKey(eq("Person"));

        getEntity("Territory").andExpect(status().isOk());
        getEntity("Person").andExpect(status().isOk());
        verifyNoMoreInteractions(entityPORepository);
    }

    @Test
    void completeSchemaIsCached() throws Exception {
        mvc.perform(get("/api/schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("*"))
                .andExpect(jsonPath("$.entities.elements.length()").value(2));

        verify(entityPORepository, times(1)).findAll();

        mvc.perform(get("/api/schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("*"))
                .andExpect(jsonPath("$.entities.elements.length()").value(2));

        verifyNoMoreInteractions(entityPORepository);
    }

    @Test
    void updatingSingleEntityUpdatesCaches() throws Exception {
        getEntity("Person").andExpect(status().isOk());
        getEntity("Person").andExpect(status().isOk())
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].explanation").isEmpty());
        verify(entityPORepository, times(1)).findByKey("Person");

        EntityPO entity = loadEntity("Person");
        entity.setExplanation("An individual human");

        mvc.perform(put("/api/schema/admin/entities/Person")
                .content(objectMapper.writeValueAsString(entity))
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Mockito.reset(entityPORepository);

        // Retrieve entity again - Cache PUT updated the ENTITY cache already
        getEntity("Person").andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("Person"))
                .andExpect(jsonPath("$.entities.elements.length()").value(2))
                .andExpect(jsonPath("$.entities.elements[?(@.key == 'Person')].explanation").value("An individual human"));

        verifyNoMoreInteractions(entityPORepository);


        // Retrieve complete schema again - Cache EVICT had to empty SCHEMA cache
        mvc.perform(get("/api/schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.meta.base").value("*"))
                .andExpect(jsonPath("$.entities.elements.length()").value(2));

        verify(entityPORepository, times(1)).findAll();
    }

    @Test
    void deletingSingleEntityUpdatesCaches() throws Exception {
        getEntity("Person").andExpect(status().isOk());
        getEntity("Person").andExpect(status().isOk());
        verify(entityPORepository, times(1)).findByKey("Person");

        mvc.perform(delete("/api/schema/admin/entities/Person")
                .header("Authorization", adminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Mockito.reset(entityPORepository);

        getEntity("Person").andExpect(status().isNotFound());
        verify(entityPORepository, times(1)).findByKey("Person");

        mvc.perform(get("/api/schema")).andExpect(status().isOk())
                .andExpect(jsonPath("$.entities.elements.length()").value(1));
        verify(entityPORepository, times(1)).findAll();
    }

}
