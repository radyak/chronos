package net.fvogel.chronosbackend.domain.schema.rest;

import net.fvogel.chronosbackend.ChronosSchemaDefinitionServiceApplication;
import net.fvogel.chronosbackend.commons.exception.NotFoundException;
import net.fvogel.chronosbackend.config.security.SecurityConfig;
import net.fvogel.chronosbackend.domain.schema.rest.mappers.ModelMapper;
import net.fvogel.chronosbackend.domain.schema.service.SchemaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SchemaController.class)
@ContextConfiguration(classes = {SecurityConfig.class, ChronosSchemaDefinitionServiceApplication.class})
public class SchemaControllerWebMvcTest {

    @MockitoBean
    private ModelMapper modelMapper;
    @MockitoBean
    private SchemaService schemaService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getItem() throws Exception {
        when(schemaService.getEntityByKey("Territory"))
                .thenThrow(new NotFoundException());

        mockMvc.perform(get("/api/schema/{key}", "Territory"))
                .andExpect(status().isNotFound());
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.name").value("Item A"));
    }


}
