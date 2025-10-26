package net.fvogel.chronosbackend.domain.person.rest;

import net.fvogel.chronosbackend.domain.person.persistence.Person;
import net.fvogel.chronosbackend.domain.person.service.PersonsService;
import net.fvogel.chronosbackend.general.wikipedia.model.WikipediaSummary;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonsController {

    private PersonsService personsService;

    public PersonsController(PersonsService personsService) {
        this.personsService = personsService;
    }

    @GetMapping
    public List<Person> all(
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "to", required = false) String to
    ) {
        return this.personsService.findBetween(from, to);
    }

    @GetMapping("/{id}")
    public Person getById(@PathVariable("id") String id) {
        return this.personsService.findById(id);
    }

    @GetMapping("/random/wikipediasummary")
    public WikipediaSummary getRandom() {
        return this.personsService.findRandom();
    }

    @GetMapping("/{id}/wikipediasummary")
    public WikipediaSummary getWikipediaSummaryById(@PathVariable("id") String id) {
        return this.personsService.findWikipediaSummaryForPerson(id);
    }

}
