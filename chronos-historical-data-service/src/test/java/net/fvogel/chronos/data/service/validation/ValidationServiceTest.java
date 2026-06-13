package net.fvogel.chronos.data.service.validation;

import net.fvogel.chronos.data.client.SchemaClient;
import net.fvogel.chronos.data.exception.SchemaValidationException;
import net.fvogel.chronos.data.model.Entry;
import net.fvogel.chronos.data.model.validation.ValidationConstraint;
import net.fvogel.chronos.data.model.validation.ValidationError;
import net.fvogel.chronos.data.service.CypherService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;

import static net.fvogel.chronos.data.testutils.DefaultTestEntries.*;
import static net.fvogel.chronos.data.testutils.MockResponseLoader.loadMockSchemaResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

    @MockitoBean
    SchemaClient schemaClient;

    @MockitoBean
    CypherService cypherService;

    @Autowired
    ValidationService validationService;

    @BeforeEach
    public void setUp() {
        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("Person.json"));
        Mockito.when(cypherService.isAttributeUnique(Mockito.any(Entry.class), Mockito.anyString()))
                .thenReturn(true);
    }

    @Test
    public void canValidateMinimalEntry() {
        Entry entry = minimalPerson();
        validationService.validate(entry);
    }

    @Test
    public void canValidateMaximalEntry() {
        Entry entry = maximalPerson();
        validationService.validate(entry);
    }

    @Test
    public void canValidateTestType() {
        Entry entry = maximalTestType();
        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("TestType.json"));
        validationService.validate(entry);
    }

    // Mandatory
    @Test
    public void throwsValidationExceptionOnMissingMandatoryAttribute() {
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
        Entry entry = maximalPerson();
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

    // Allowed Values: Scalar values
    @Test
    public void throwsValidationExceptionOnUnAllowedValue() {
        Entry entry = maximalPerson();
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

    // Allowed Values: Array value
    @Test
    public void throwsValidationExceptionOnUnAllowedValueInArrayAttribute() {
        Entry entry = maximalTestType();
        entry.getAttributes().put("enum-array-attr", Set.of("arrayVal2", "not-allowed-value"));

        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("TestType.json"));

        try {
            validationService.validate(entry);
            Assertions.fail();
        } catch (SchemaValidationException sve) {
            assertThat(sve.getValidationErrors().size(), is(1));

            ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
            assertThat(validationError.getConstraint(), is(ValidationConstraint.ALLOWED_VALUES));
            assertThat(validationError.getPath(), is("attributes[enum-array-attr]"));
            assertThat(validationError.getValue(), is("not-allowed-value"));
        }
    }

    // Uniqueness
    @Test
    public void throwsValidationExceptionOnNonUniqueValue() {
        Entry entry = maximalPerson();

        // NOTE: UNIQUE rule is tested integratively
        Mockito.when(cypherService.isAttributeUnique(Mockito.any(Entry.class), Mockito.eq("key")))
                .thenReturn(false);

        try {
            validationService.validate(entry);
            Assertions.fail();
        } catch (SchemaValidationException sve) {
            assertThat(sve.getValidationErrors().size(), is(1));

            ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
            assertThat(validationError.getConstraint(), is(ValidationConstraint.UNIQUE));
            assertThat(validationError.getPath(), is("attributes[key]"));
            assertThat(validationError.getValue(), is("test-person"));
        }
    }

    // Is Array
    @Test
    public void throwsValidationExceptionOnNonArrayValue() {
        Entry entry = maximalTestType();
        entry.getAttributes().put("enum-array-attr", "arrayVal2");

        Mockito.when(schemaClient.getType(Mockito.anyString()))
                .thenReturn(loadMockSchemaResponse("TestType.json"));

        try {
            validationService.validate(entry);
            Assertions.fail();
        } catch (SchemaValidationException sve) {
            assertThat(sve.getValidationErrors().size(), is(1));

            ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
            assertThat(validationError.getConstraint(), is(ValidationConstraint.IS_ARRAY));
            assertThat(validationError.getPath(), is("attributes[enum-array-attr]"));
            assertThat(validationError.getValue(), is("arrayVal2"));
        }
    }

    // Correct Types
    @Nested
    public class CorrectTypes {
        // Type: String
        @Test
        public void throwsValidationExceptionOnStringValueInconsistency() {
            Mockito.when(schemaClient.getType(Mockito.anyString()))
                    .thenReturn(loadMockSchemaResponse("Person.json"));

            Entry entry = maximalPerson();
            entry.getAttributes().put("name", 17);
            try {
                validationService.validate(entry);
                Assertions.fail();
            } catch (SchemaValidationException sve) {
                assertThat(sve.getValidationErrors().size(), is(1));

                ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
                assertThat(validationError.getConstraint(), is(ValidationConstraint.CORRECT_TYPE));
                assertThat(validationError.getPath(), is("attributes[name]"));
                assertThat(validationError.getValue(), is(17));
            }
        }

        // Type: Date notation
        @Test
        public void throwsValidationExceptionOnDateValueInconsistency() {
            Mockito.when(schemaClient.getType(Mockito.anyString()))
                    .thenReturn(loadMockSchemaResponse("Person.json"));

            Entry entry = maximalPerson();
            entry.getAttributes().put("start", "1745, July");
            try {
                validationService.validate(entry);
                Assertions.fail();
            } catch (SchemaValidationException sve) {
                assertThat(sve.getValidationErrors().size(), is(1));

                ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
                assertThat(validationError.getConstraint(), is(ValidationConstraint.CORRECT_TYPE));
                assertThat(validationError.getPath(), is("attributes[start]"));
                assertThat(validationError.getValue(), is("1745, July"));
            }
        }

        // Type: Date enum
        @Test
        public void throwsValidationExceptionOnWikiqidValueInconsistency() {
            Mockito.when(schemaClient.getType(Mockito.anyString()))
                    .thenReturn(loadMockSchemaResponse("Person.json"));

            Entry entry = maximalPerson();
            entry.getAttributes().put("wikiqid", "P1234");
            try {
                validationService.validate(entry);
                Assertions.fail();
            } catch (SchemaValidationException sve) {
                assertThat(sve.getValidationErrors().size(), is(1));

                ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
                assertThat(validationError.getConstraint(), is(ValidationConstraint.CORRECT_TYPE));
                assertThat(validationError.getPath(), is("attributes[wikiqid]"));
                assertThat(validationError.getValue(), is("P1234"));
            }
        }

        // Type: Date enum
        @Test
        public void throwsValidationExceptionOnNumberValueInconsistency() {
            Mockito.when(schemaClient.getType(Mockito.anyString()))
                    .thenReturn(loadMockSchemaResponse("Person.json"));

            Entry entry = maximalPerson();
            entry.getAttributes().put("height", "178");
            try {
                validationService.validate(entry);
                Assertions.fail();
            } catch (SchemaValidationException sve) {
                assertThat(sve.getValidationErrors().size(), is(1));

                ValidationError validationError = sve.getValidationErrors().stream().findFirst().get();
                assertThat(validationError.getConstraint(), is(ValidationConstraint.CORRECT_TYPE));
                assertThat(validationError.getPath(), is("attributes[height]"));
                assertThat(validationError.getValue(), is("178"));
            }
        }
    }

}
