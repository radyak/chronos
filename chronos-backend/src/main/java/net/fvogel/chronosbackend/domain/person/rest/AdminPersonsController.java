package net.fvogel.chronosbackend.domain.person.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.domain.person.persistence.Person;
import net.fvogel.chronosbackend.domain.person.service.PersonsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/persons")
public class AdminPersonsController {

    private PersonsService personsService;

    public AdminPersonsController(PersonsService personsService) {
        this.personsService = personsService;
    }

    @PostMapping
    public Person create(@Valid @RequestBody Person entry) {
        return this.personsService.save(entry);
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

    @PutMapping
    public Person update(@Valid @RequestBody Person entry) {
        return this.personsService.save(entry);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        this.personsService.deleteById(id);
    }

}
