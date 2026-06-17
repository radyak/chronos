package net.fvogel.chronos.data.it.admin;

import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.dto.UniqueCheckDto;
import net.fvogel.chronos.data.testutils.BaseIntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class AdminDataApiUniqueCheckIntegrationTest extends BaseIntegrationTest {

    @Container
    @ServiceConnection
    public static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5");

    @Test
    void canCertifyUniquenessForAnExistingEntryKey() throws Exception {
        Entry entry = dataService.findByKey("vespasian").get();
        uniqueCheck(UniqueCheckDto.builder()
                        .key("key")
                        .value("vespasian")
                        .elementId(entry.getElementId())
                        .build(),
                true);
    }

    @Test
    void canCertifyUniquenessForNewKeyForAnExistingEntryKey() throws Exception {
        Entry entry = dataService.findByKey("vespasian").get();
        uniqueCheck(UniqueCheckDto.builder()
                        .key("key")
                        .value("vespasian-changed")
                        .elementId(entry.getElementId())
                        .build(),
                true);
    }

    @Test
    void canCertifyNonUniquenessForNewEntryWithAlreadyExistingKey() throws Exception {
        // Variant: elementId = null
        uniqueCheck(UniqueCheckDto.builder()
                        .key("key")
                        .value("vespasian")
                        .elementId(null)
                        .build(),
                false);

        // Variant: elementId = ''
        uniqueCheck(UniqueCheckDto.builder()
                        .key("key")
                        .value("vespasian")
                        .elementId("")
                        .build(),
                false);
    }

    @Test
    void canCertifyUniquenessForNewEntryWithNewKey() throws Exception {
        uniqueCheck(UniqueCheckDto.builder()
                        .key("key")
                        .value("trajan")
                        .elementId(null)
                        .build(),
                true);
    }

    @Test
    void canCertifyUniquenessForNullValues() throws Exception {
        uniqueCheck(UniqueCheckDto.builder()
                        .key("key")
                        .value(null)
                        .elementId(null)
                        .build(),
                true);
    }

    @Test
    void throwsBadRequestOnMissingKey() throws Exception {
        // Variant: new key mentioned
        mvc.perform(
                        get("/api/data/admin/unique?value={value}&elementId={elementId}",
                                "trajan",
                                null
                        ).header("Authorization", adminAuthHeader())
                )
                .andExpect(status().isBadRequest());

        // Variant: key=null submitted
        mvc.perform(
                        get("/api/data/admin/unique?key={key}&value={value}&elementId={elementId}",
                                null,
                                "trajan",
                                null
                        ).header("Authorization", adminAuthHeader())
                )
                .andExpect(status().isBadRequest());

        // Variant: key='' submitted
        mvc.perform(
                        get("/api/data/admin/unique?key={key}&value={value}&elementId={elementId}",
                                "",
                                "trajan",
                                null
                        ).header("Authorization", adminAuthHeader())
                )
                .andExpect(status().isBadRequest());
    }

    void uniqueCheck(UniqueCheckDto uniqueCheckDto, boolean isUnique) throws Exception {
        mvc.perform(
                        get("/api/data/admin/unique?key={key}&value={value}&elementId={elementId}",
                                uniqueCheckDto.getKey(),
                                uniqueCheckDto.getValue(),
                                uniqueCheckDto.getElementId()
                        ).header("Authorization", adminAuthHeader())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("" + isUnique));
    }

    @Nested
    public class SecurityTest {

        @Test
        void unauthenticatedUserCannotCheckUniqueness() throws Exception {
            mvc.perform(
                    get("/api/data/admin/unique?key={key}&value={value}&elementId={elementId}",
                            "key", "vespasian", "null"
                    )
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void unauthorizedUserCannotCheckUniqueness() throws Exception {
            mvc.perform(
                    get("/api/data/admin/unique?key={key}&value={value}&elementId={elementId}",
                            "key", "vespasian", "null"
                    ).header("Authorization", authHeader("user"))
            ).andExpect(status().isForbidden());
        }

    }

}
