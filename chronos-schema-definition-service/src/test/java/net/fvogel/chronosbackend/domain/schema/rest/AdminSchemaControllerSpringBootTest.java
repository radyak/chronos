package net.fvogel.chronosbackend.domain.schema.rest;

import net.fvogel.chronosbackend.commons.security.DevJwtGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "test-data"})
public class AdminSchemaControllerSpringBootTest {

    @Value("${app.auth.admin-role}")
    String adminRole;

    @Autowired
    DevJwtGenerator jwtGenerator;

    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void deleteItem() throws Exception {
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteItemAsAdmin() throws Exception {
        mvc.perform(delete("/api/schema/admin/entities/{key}", "Territory")
                                .header("Authorization", "Bearer " + jwtGenerator.generateJWT("admin", Set.of(adminRole)))
//                        .with(jwt().jwt(jwt -> jwt.subject("schema-admin").claim("resource_accessX", adminRole)))
                )
                .andExpect(status().isOk());
    }


    private String obtainAccessToken(String username, String password) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "fooClientIdPassword");
        params.add("username", username);
        params.add("password", password);

        ResultActions result
                = mvc.perform(post("/oauth/token")
                        .params(params)
                        .with(httpBasic("fooClientIdPassword", "secret"))
                        .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }
}
