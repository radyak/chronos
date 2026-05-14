package net.fvogel.chronos.data.REFACTORING.deprecated;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class LabelledEntity extends Entity {
    public Set<String> labels;
}
