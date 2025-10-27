package net.fvogel.chronosbackend.domain.person.rest;

import net.fvogel.chronosbackend.domain.person.persistence.Person;
import net.fvogel.chronosbackend.domain.person.service.PersonsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonsController {

    private final PersonsService personsService;

    public PersonsController(PersonsService personsService) {
        this.personsService = personsService;
    }

    @GetMapping
    public List<Person> query(
            @RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "to", required = false) String to
    ) {
        return this.personsService.findBetween(from, to);
    }

    @GetMapping("/{identifier}")
    public Person getByIdOrKey(@PathVariable("identifier") String identifier) {
        return this.personsService.findByIdOrKey(identifier);
    }

}
