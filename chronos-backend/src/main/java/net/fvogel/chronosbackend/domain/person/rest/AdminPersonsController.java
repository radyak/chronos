package net.fvogel.chronosbackend.domain.person.rest;

import jakarta.validation.Valid;
import net.fvogel.chronosbackend.domain.person.persistence.Person;
import net.fvogel.chronosbackend.domain.person.service.PersonsService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/persons")
public class AdminPersonsController {

    private final PersonsService personsService;

    public AdminPersonsController(PersonsService personsService) {
        this.personsService = personsService;
    }

    @PostMapping
    public Person create(@Valid @RequestBody Person person) {
        return this.personsService.save(person);
    }

    @PutMapping
    public Person update(@Valid @RequestBody Person person) {
        return this.personsService.save(person);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        this.personsService.deleteById(id);
    }

}
