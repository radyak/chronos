package net.fvogel.chronos.data.domain.person.rest;

import jakarta.validation.Valid;
import net.fvogel.chronos.data.domain.person.persistence.ChildOf;
import net.fvogel.chronos.data.domain.person.persistence.Person;
import net.fvogel.chronos.data.domain.person.service.PersonParentsService;
import net.fvogel.chronos.data.domain.person.service.PersonsService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/persons/{id}/parents")
public class AdminPersonParentsRelationsController {

    private final PersonsService personsService;
    private final PersonParentsService personParentsService;

    public AdminPersonParentsRelationsController(
            PersonsService personsService,
            PersonParentsService personParentsService) {
        this.personsService = personsService;
        this.personParentsService = personParentsService;
    }

    @PostMapping
    public Person add(
            @PathVariable("id") String id,
            @Valid @RequestBody ChildOf childOf
    ) {
        Person child = this.personsService.findById(id);
        return this.personParentsService.addChildOf(child, childOf);
    }

    @PutMapping("/{parentId}")
    public Person update(
            @PathVariable("id") String childId,
            @PathVariable("parentId") String parentId,
            @Valid @RequestBody ChildOf childOf
    ) {
        Person child = this.personsService.findById(childId);
        return this.personParentsService.updateChildOf(child, parentId, childOf);
    }

    @DeleteMapping("/{parentId}")
    public void delete(
            @PathVariable("id") String childId,
            @PathVariable("parentId") String parentId
    ) {
        this.personParentsService.deleteChildOf(childId, parentId);
    }

}
