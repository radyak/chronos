package net.fvogel.chronosbackend.domain.generic.persistence;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
public class LabelledEntity extends Entity {
    public Set<String> labels;
}
