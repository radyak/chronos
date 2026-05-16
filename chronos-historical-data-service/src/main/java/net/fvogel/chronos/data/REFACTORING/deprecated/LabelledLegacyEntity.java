package net.fvogel.chronos.data.REFACTORING.deprecated;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class LabelledLegacyEntity extends LegacyEntity {
    public Set<String> labels;
}
