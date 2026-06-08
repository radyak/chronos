package net.fvogel.chronos.data.service.validation;

import net.fvogel.chronos.data.client.SchemaClient;
import net.fvogel.chronos.data.exception.SchemaValidationException;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationConstraint;
import net.fvogel.chronos.data.model.validation.ValidationError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static net.fvogel.chronos.data.testutils.DefaultTestEntries.maximalPerson;
import static net.fvogel.chronos.data.testutils.DefaultTestEntries.minimalPerson;
import static net.fvogel.chronos.data.testutils.MockResponseLoader.loadMockSchemaResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

    @MockitoBean
    SchemaClient schemaClient;
    @Autowired
    ValidationService validationService;

    @Test
    public void canValidateMinimalEntry() {
        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("Person.json"));

        Entry entry = minimalPerson();
        validationService.validate(entry);
    }

    @Test
    public void canValidateMaximalEntry() {
        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("Person.json"));

        Entry entry = maximalPerson();
        validationService.validate(entry);
    }

    // Mandatory
    @Test
    public void throwsValidationExceptionOnMissingMandatoryAttribute() {
        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("Person.json"));

        Entry entry = maximalPerson();
        entry.getAttributes().remove("key");
        try {
            validationService.validate(entry);
            Assertions.fail();
        } catch (SchemaValidationException sve) {
            assertThat(sve.getValidationErrors().size(), is(1));

            ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
            assertThat(validationError.getConstraint(), is(ValidationConstraint.MANDATORY));
            assertThat(validationError.getPath(), is("attributes[key]"));
            assertThat(validationError.getValue(), nullValue());
        }
    }

    // Defined Attributes
    @Test
    public void throwsValidationExceptionOnUnDefinedAttribute() {
        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("Person.json"));

        Entry entry = minimalPerson();
        entry.getAttributes().put("undefined-attribute", "abc");
        try {
            validationService.validate(entry);
            Assertions.fail();
        } catch (SchemaValidationException sve) {
            assertThat(sve.getValidationErrors().size(), is(1));

            ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
            assertThat(validationError.getConstraint(), is(ValidationConstraint.DEFINED_ATTRIBUTES));
            assertThat(validationError.getPath(), is("attributes[undefined-attribute]"));
            assertThat(validationError.getValue(), is("abc"));
        }
    }

    // Allowed Values
    @Test
    public void throwsValidationExceptionOnUnAllowedValue() {
        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("Person.json"));

        Entry entry = minimalPerson();
        entry.getAttributes().put("gender", "FEMALE");
        try {
            validationService.validate(entry);
            Assertions.fail();
        } catch (SchemaValidationException sve) {
            assertThat(sve.getValidationErrors().size(), is(1));

            ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
            assertThat(validationError.getConstraint(), is(ValidationConstraint.ALLOWED_VALUES));
            assertThat(validationError.getPath(), is("attributes[gender]"));
            assertThat(validationError.getValue(), is("FEMALE"));
        }
    }

}
