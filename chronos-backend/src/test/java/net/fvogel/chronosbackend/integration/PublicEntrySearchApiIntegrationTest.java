package net.fvogel.chronosbackend.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fvogel.chronosbackend.common.persistence.entries.model.Entry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(
		scripts = "/import-test-data.sql",
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
class PublicEntrySearchApiIntegrationTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void whenGetEntriesWithoutFilter_thenAllEntriesReturned()
			throws Exception {
		mvc.perform(get("/api/entries")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(entriesCount(7));
	}

	@Test
	public void whenFilterEntriesByTitlePart_thenEntriesWithCaseInsensitivePartInTitleReturned()
			throws Exception {
		mvc.perform(get("/api/entries?title=LeMagn")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(entriesContainAllTitles("Charlemagne"));
	}

	@Test
	public void whenFilterEntriesByTags_thenOnlyEntriesWithTagsReturned()
			throws Exception {
		// Tags:
		//	3=Carolingian
		//  1=Emperor
		mvc.perform(get("/api/entries?tags=3,1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(entriesContainAllTitles("Charlemagne", "Louis"));
	}

	@Test
	public void whenFilterEntriesByStartYear_thenOnlyEntriesWithAtLeastOneRangeOverlappingWithStartYearReturned()
			throws Exception {
		mvc.perform(get("/api/entries?from=760")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(entriesContainAllTitles("Pepin", "Charlemagne", "Louis"));
	}

	@Test
	public void whenFilterEntriesByEndYear_thenOnlyEntriesWithAtLeastOneRangeOverlappingWithEndYearReturned()
			throws Exception {
		mvc.perform(get("/api/entries?to=700")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(entriesContainAllTitles("Charles Martel", "Tiberius", "Augustus"));
	}

	@Test
	@Disabled("TODO: Debugging why negative years seem to be processed as if they were '0'")
	public void whenFilterEntriesByEndYearNegative_thenOnlyEntriesWithAtLeastOneRangeOverlappingWithEndYearReturned()
			throws Exception {
		mvc.perform(get("/api/entries?to=-44")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(entriesContainAllTitles("Augustus"));
	}

	@Test
	public void whenFilterEntriesByYearRange_thenOnlyEntriesWithAtLeastOnCompleteRangeInsideReturned()
			throws Exception {
		mvc.perform(get("/api/entries?from=-42&to=100")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(entriesContainAllTitles("Augustus", "Tiberius"));
	}

	@Test
	public void whenFilterEntriesByYearRangeAndCategory_thenOnlyMatchingEntriesReturned()
			throws Exception {
		mvc.perform(get("/api/entries?from=-42&to=830&tags=1")	// Tags #1 = Emperor
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(entriesContainAllTitles("Augustus", "Tiberius", "Charlemagne", "Louis"));
	}

	private ResultMatcher entriesContainAllTitles(String ...titles) {
		return mvcResult -> {
			Set<String> expected = new HashSet<>(Arrays.asList(titles));

			Set<String> actual = fromMvcResult(mvcResult).stream()
					.map(e -> e.getTitle())
					.collect(Collectors.toSet());
			if (!actual.equals(expected)) {
				throw new AssertionError(String.format(
						"Expected %s as titles but got %s",
						expected,
						actual
				));
			}
		};
	}

	private ResultMatcher entriesCount(int count) {
		return mvcResult -> {
			Set<Entry> entries = fromMvcResult(mvcResult);
			if (entries.size() != count) {
				throw new AssertionError(String.format("Expected %o entries, but got %o", count, entries.size()));
			}
		};
	}

	private Set<Entry> fromMvcResult(MvcResult mvcResult) {
		String contentAsString = null;
		try {
			contentAsString = mvcResult.getResponse().getContentAsString();
			return objectMapper.readValue(contentAsString, new TypeReference<HashSet<Entry>>(){});
		} catch (UnsupportedEncodingException | JsonProcessingException e) {
			throw new AssertionError(
					"API response not parseable to Entry[]" + (contentAsString != null ? (": " + contentAsString) : ""),
					e
			);
		}
    }
}
