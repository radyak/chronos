package net.fvogel.chronosbackend.domain.generic.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelledEntity extends Entity {
    Set<String> labels;
}
